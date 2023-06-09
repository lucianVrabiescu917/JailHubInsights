import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ActivityFormService, ActivityFormGroup } from './activity-form.service';
import { IActivity } from '../activity.model';
import { ActivityService } from '../service/activity.service';
import { IPrison } from 'app/entities/prison/prison.model';
import { PrisonService } from 'app/entities/prison/service/prison.service';
import { ActivityType } from 'app/entities/enumerations/activity-type.model';
import { IStaff } from '../../staff/staff.model';
import { StaffService } from '../../staff/service/staff.service';
import { IInmate } from '../../inmate/inmate.model';
import { InmateService } from '../../inmate/service/inmate.service';

@Component({
  selector: 'jhi-activity-update',
  templateUrl: './activity-update.component.html',
})
export class ActivityUpdateComponent implements OnInit {
  isSaving = false;
  activity: IActivity | null = null;
  activityTypeValues = Object.keys(ActivityType);

  prisonsSharedCollection: IPrison[] = [];
  staffSharedCollection: IStaff[] = [];
  inmatesSharedCollection: IInmate[] = [];

  editForm: ActivityFormGroup = this.activityFormService.createActivityFormGroup();

  constructor(
    protected activityService: ActivityService,
    protected activityFormService: ActivityFormService,
    protected prisonService: PrisonService,
    protected staffService: StaffService,
    protected inmateService: InmateService,
    protected activatedRoute: ActivatedRoute
  ) {}

  comparePrison = (o1: IPrison | null, o2: IPrison | null): boolean => this.prisonService.comparePrison(o1, o2);

  compareStaff = (o1: IStaff | null, o2: IStaff | null): boolean => this.staffService.compareStaff(o1, o2);

  compareInmates = (o1: IInmate | null, o2: IInmate | null): boolean => this.inmateService.compareInmate(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ activity }) => {
      this.activity = activity;
      if (activity) {
        this.updateForm(activity);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const activity = this.activityFormService.getActivity(this.editForm);
    if (activity.id !== null) {
      this.subscribeToSaveResponse(this.activityService.update(activity));
    } else {
      this.subscribeToSaveResponse(this.activityService.create(activity));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IActivity>>): void {
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

  protected updateForm(activity: IActivity): void {
    this.activity = activity;
    this.activityFormService.resetForm(this.editForm, activity);

    this.prisonsSharedCollection = this.prisonService.addPrisonToCollectionIfMissing<IPrison>(
      this.prisonsSharedCollection,
      activity.prison
    );

    this.staffSharedCollection = this.staffService.addStaffToCollectionIfMissing<IStaff>(
      this.staffSharedCollection,
      ...(activity.staff ?? [])
    );

    this.inmatesSharedCollection = this.inmateService.addInmateToCollectionIfMissing<IInmate>(
      this.inmatesSharedCollection,
      ...(activity.inmates ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.prisonService
      .query()
      .pipe(map((res: HttpResponse<IPrison[]>) => res.body ?? []))
      .pipe(map((prisons: IPrison[]) => this.prisonService.addPrisonToCollectionIfMissing<IPrison>(prisons, this.activity?.prison)))
      .subscribe((prisons: IPrison[]) => (this.prisonsSharedCollection = prisons));

    this.staffService
      .query()
      .pipe(map((res: HttpResponse<IStaff[]>) => res.body ?? []))
      .pipe(map((staff: IStaff[]) => this.staffService.addStaffToCollectionIfMissing<IStaff>(staff, ...(this.activity?.staff ?? []))))
      .subscribe((staff: IStaff[]) => (this.staffSharedCollection = staff));

    this.inmateService
      .query()
      .pipe(map((res: HttpResponse<IInmate[]>) => res.body ?? []))
      .pipe(
        map((inmates: IInmate[]) => this.inmateService.addInmateToCollectionIfMissing<IInmate>(inmates, ...(this.activity?.inmates ?? [])))
      )
      .subscribe((inmates: IInmate[]) => (this.inmatesSharedCollection = inmates));
  }
}
