import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { InmateFormGroup, InmateFormService } from './inmate-form.service';
import { IInmate } from '../inmate.model';
import { InmateService } from '../service/inmate.service';
import { IPrison } from 'app/entities/prison/prison.model';
import { PrisonService } from 'app/entities/prison/service/prison.service';
import { IArea } from 'app/entities/area/area.model';
import { AreaService } from 'app/entities/area/service/area.service';
import { IActivity } from 'app/entities/activity/activity.model';
import { ActivityService } from 'app/entities/activity/service/activity.service';
import { AreaType } from '../../enumerations/area-type.model';

@Component({
  selector: 'jhi-inmate-update',
  templateUrl: './inmate-update.component.html',
})
export class InmateUpdateComponent implements OnInit {
  isSaving = false;
  inmate: IInmate | null = null;

  prisonsSharedCollection: IPrison[] = [];
  areasSharedCollection: IArea[] = [];
  activitiesSharedCollection: IActivity[] = [];
  uploadedImage: File | null = null;

  editForm: InmateFormGroup = this.inmateFormService.createInmateFormGroup();

  selectedImage: string | ArrayBuffer | null = null;

  constructor(
    protected inmateService: InmateService,
    protected inmateFormService: InmateFormService,
    protected prisonService: PrisonService,
    protected areaService: AreaService,
    protected activityService: ActivityService,
    protected activatedRoute: ActivatedRoute
  ) {}

  comparePrison = (o1: IPrison | null, o2: IPrison | null): boolean => this.prisonService.comparePrison(o1, o2);

  compareArea = (o1: IArea | null, o2: IArea | null): boolean => this.areaService.compareArea(o1, o2);

  compareActivity = (o1: IActivity | null, o2: IActivity | null): boolean => this.activityService.compareActivity(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ inmate }) => {
      this.inmate = inmate;
      if (this.inmate != null && this.inmate.image != undefined) {
        this.selectedImage = this.inmate.image;
      }
      if (inmate) {
        this.updateForm(inmate);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const inmate = this.inmateFormService.getInmate(this.editForm);
    inmate.image = this.selectedImage as string;
    if (inmate.id !== null) {
      this.subscribeToSaveResponse(this.inmateService.update(inmate));
    } else {
      this.subscribeToSaveResponse(this.inmateService.create(inmate));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInmate>>): void {
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

  protected updateForm(inmate: IInmate): void {
    this.inmate = inmate;
    this.inmateFormService.resetForm(this.editForm, inmate);

    this.prisonsSharedCollection = this.prisonService.addPrisonToCollectionIfMissing<IPrison>(this.prisonsSharedCollection, inmate.prison);
    this.areasSharedCollection = this.areaService.addAreaToCollectionIfMissing<IArea>(this.areasSharedCollection, inmate.assignedCell);
    this.activitiesSharedCollection = this.activityService.addActivityToCollectionIfMissing<IActivity>(
      this.activitiesSharedCollection,
      ...(inmate.activities ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.prisonService
      .query()
      .pipe(map((res: HttpResponse<IPrison[]>) => res.body ?? []))
      .pipe(map((prisons: IPrison[]) => this.prisonService.addPrisonToCollectionIfMissing<IPrison>(prisons, this.inmate?.prison)))
      .subscribe((prisons: IPrison[]) => (this.prisonsSharedCollection = prisons));

    this.areaService
      .query()
      .pipe(map((res: HttpResponse<IArea[]>) => res.body ?? []))
      .pipe(map((areas: IArea[]) => this.areaService.addAreaToCollectionIfMissing<IArea>(areas, this.inmate?.assignedCell)))
      .subscribe((areas: IArea[]) => (this.areasSharedCollection = areas.filter(area => area.areaType === AreaType.CELL)));

    this.activityService
      .query()
      .pipe(map((res: HttpResponse<IActivity[]>) => res.body ?? []))
      .pipe(
        map((activities: IActivity[]) =>
          this.activityService.addActivityToCollectionIfMissing<IActivity>(activities, ...(this.inmate?.activities ?? []))
        )
      )
      .subscribe((activities: IActivity[]) => (this.activitiesSharedCollection = activities));
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];

    if (file) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.uploadedImage = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  onImageSelected(image: string) {
    this.selectedImage = image;
  }
}
