import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { StaffFormService, StaffFormGroup } from './staff-form.service';
import { IStaff } from '../staff.model';
import { StaffService } from '../service/staff.service';
import { IPrison } from 'app/entities/prison/prison.model';
import { PrisonService } from 'app/entities/prison/service/prison.service';
import { IActivity } from 'app/entities/activity/activity.model';
import { ActivityService } from 'app/entities/activity/service/activity.service';
import { StaffType } from 'app/entities/enumerations/staff-type.model';

@Component({
  selector: 'jhi-staff-update',
  templateUrl: './staff-update.component.html',
})
export class StaffUpdateComponent implements OnInit {
  isSaving = false;
  staff: IStaff | null = null;
  staffTypeValues = Object.keys(StaffType);

  prisonsSharedCollection: IPrison[] = [];
  activitiesSharedCollection: IActivity[] = [];

  editForm: StaffFormGroup = this.staffFormService.createStaffFormGroup();

  constructor(
    protected staffService: StaffService,
    protected staffFormService: StaffFormService,
    protected prisonService: PrisonService,
    protected activityService: ActivityService,
    protected activatedRoute: ActivatedRoute
  ) {}

  comparePrison = (o1: IPrison | null, o2: IPrison | null): boolean => this.prisonService.comparePrison(o1, o2);

  compareActivity = (o1: IActivity | null, o2: IActivity | null): boolean => this.activityService.compareActivity(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ staff }) => {
      this.staff = staff;
      if (staff) {
        this.updateForm(staff);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const staff = this.staffFormService.getStaff(this.editForm);
    if (staff.id !== null) {
      this.subscribeToSaveResponse(this.staffService.update(staff));
    } else {
      this.subscribeToSaveResponse(this.staffService.create(staff));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStaff>>): void {
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

  protected updateForm(staff: IStaff): void {
    this.staff = staff;
    this.staffFormService.resetForm(this.editForm, staff);

    this.prisonsSharedCollection = this.prisonService.addPrisonToCollectionIfMissing<IPrison>(this.prisonsSharedCollection, staff.prison);
    this.activitiesSharedCollection = this.activityService.addActivityToCollectionIfMissing<IActivity>(
      this.activitiesSharedCollection,
      ...(staff.activities ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.prisonService
      .query()
      .pipe(map((res: HttpResponse<IPrison[]>) => res.body ?? []))
      .pipe(map((prisons: IPrison[]) => this.prisonService.addPrisonToCollectionIfMissing<IPrison>(prisons, this.staff?.prison)))
      .subscribe((prisons: IPrison[]) => (this.prisonsSharedCollection = prisons));

    this.activityService
      .query()
      .pipe(map((res: HttpResponse<IActivity[]>) => res.body ?? []))
      .pipe(
        map((activities: IActivity[]) =>
          this.activityService.addActivityToCollectionIfMissing<IActivity>(activities, ...(this.staff?.activities ?? []))
        )
      )
      .subscribe((activities: IActivity[]) => (this.activitiesSharedCollection = activities));
  }
}
