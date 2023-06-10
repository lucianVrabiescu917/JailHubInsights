import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { AreaFormService, AreaFormGroup } from './area-form.service';
import { IArea } from '../area.model';
import { AreaService } from '../service/area.service';
import { IPrison } from 'app/entities/prison/prison.model';
import { PrisonService } from 'app/entities/prison/service/prison.service';
import { IStaff } from 'app/entities/staff/staff.model';
import { StaffService } from 'app/entities/staff/service/staff.service';
import { AreaType } from 'app/entities/enumerations/area-type.model';

@Component({
  selector: 'jhi-area-update',
  templateUrl: './area-update.component.html',
})
export class AreaUpdateComponent implements OnInit {
  isSaving = false;
  area: IArea | null = null;
  areaTypeValues = Object.keys(AreaType);

  prisonsSharedCollection: IPrison[] = [];
  staffSharedCollection: IStaff[] = [];
  areasSharedCollection: IArea[] = [];
  allStaff: number[] = [];
  areaRatioLevel: number = 0;

  editForm: AreaFormGroup = this.areaFormService.createAreaFormGroup();

  constructor(
    protected areaService: AreaService,
    protected areaFormService: AreaFormService,
    protected prisonService: PrisonService,
    protected staffService: StaffService,
    protected activatedRoute: ActivatedRoute
  ) {}

  comparePrison = (o1: IPrison | null, o2: IPrison | null): boolean => this.prisonService.comparePrison(o1, o2);

  compareStaff = (o1: IStaff | null, o2: IStaff | null): boolean => this.staffService.compareStaff(o1, o2);

  compareArea = (o1: IArea | null, o2: IArea | null): boolean => this.areaService.compareArea(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ area }) => {
      this.loadRelationshipsOptions();

      this.area = area;
      if (area) {
        this.updateForm(area);
        this.areaService
          .queryStaff(area)
          .pipe(map((res: HttpResponse<number[]>) => res.body ?? []))
          .subscribe((staff: number[]) => (this.allStaff = staff));
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const area = this.areaFormService.getArea(this.editForm);
    if (area.id !== null) {
      this.subscribeToSaveResponse(this.areaService.update(area));
    } else {
      this.subscribeToSaveResponse(this.areaService.create(area));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IArea>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(area: IArea): void {
    this.area = area;
    this.areaFormService.resetForm(this.editForm, area);

    this.prisonsSharedCollection = this.prisonService.addPrisonToCollectionIfMissing<IPrison>(this.prisonsSharedCollection, area.prison);
    this.staffSharedCollection = this.staffService.addStaffToCollectionIfMissing<IStaff>(
      this.staffSharedCollection,
      ...(area.assignedStaffAreas ?? [])
    );

    this.areasSharedCollection = this.areaService.addAreaToCollectionIfMissing<IArea>(
      this.areasSharedCollection,
      ...(area.composedOfAreas ?? [])
    );

    this.areasSharedCollection = this.areasSharedCollection.filter(area => area.id != this.area?.id);
  }

  protected loadRelationshipsOptions(): void {
    this.prisonService
      .query()
      .pipe(map((res: HttpResponse<IPrison[]>) => res.body ?? []))
      .pipe(map((prisons: IPrison[]) => this.prisonService.addPrisonToCollectionIfMissing<IPrison>(prisons, this.area?.prison)))
      .subscribe((prisons: IPrison[]) => (this.prisonsSharedCollection = prisons));

    this.staffService
      .queryAll()
      .pipe(map((res: HttpResponse<IStaff[]>) => res.body ?? []))
      .pipe(
        map((staff: IStaff[]) => this.staffService.addStaffToCollectionIfMissing<IStaff>(staff, ...(this.area?.assignedStaffAreas ?? [])))
      )
      .subscribe((staff: IStaff[]) => (this.staffSharedCollection = staff));

    this.areaService
      .query()
      .pipe(map((res: HttpResponse<IArea[]>) => res.body ?? []))
      .pipe(map((areas: IArea[]) => this.areaService.addAreaToCollectionIfMissing<IArea>(areas, ...(this.area?.composedOfAreas ?? []))))
      .subscribe((areas: IArea[]) => (this.areasSharedCollection = areas.filter(area => area.id != this.area?.id)));
  }
}
