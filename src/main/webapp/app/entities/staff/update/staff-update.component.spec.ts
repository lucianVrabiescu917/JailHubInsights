import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { StaffFormService } from './staff-form.service';
import { StaffService } from '../service/staff.service';
import { IStaff } from '../staff.model';
import { IPrison } from 'app/entities/prison/prison.model';
import { PrisonService } from 'app/entities/prison/service/prison.service';
import { IActivity } from 'app/entities/activity/activity.model';
import { ActivityService } from 'app/entities/activity/service/activity.service';

import { StaffUpdateComponent } from './staff-update.component';

describe('Staff Management Update Component', () => {
  let comp: StaffUpdateComponent;
  let fixture: ComponentFixture<StaffUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let staffFormService: StaffFormService;
  let staffService: StaffService;
  let prisonService: PrisonService;
  let activityService: ActivityService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [StaffUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(StaffUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StaffUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    staffFormService = TestBed.inject(StaffFormService);
    staffService = TestBed.inject(StaffService);
    prisonService = TestBed.inject(PrisonService);
    activityService = TestBed.inject(ActivityService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Prison query and add missing value', () => {
      const staff: IStaff = { id: 456 };
      const prison: IPrison = { id: 16115 };
      staff.prison = prison;

      const prisonCollection: IPrison[] = [{ id: 70660 }];
      jest.spyOn(prisonService, 'query').mockReturnValue(of(new HttpResponse({ body: prisonCollection })));
      const additionalPrisons = [prison];
      const expectedCollection: IPrison[] = [...additionalPrisons, ...prisonCollection];
      jest.spyOn(prisonService, 'addPrisonToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ staff });
      comp.ngOnInit();

      expect(prisonService.query).toHaveBeenCalled();
      expect(prisonService.addPrisonToCollectionIfMissing).toHaveBeenCalledWith(
        prisonCollection,
        ...additionalPrisons.map(expect.objectContaining)
      );
      expect(comp.prisonsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Activity query and add missing value', () => {
      const staff: IStaff = { id: 456 };
      const activities: IActivity[] = [{ id: 51755 }];
      staff.activities = activities;

      const activityCollection: IActivity[] = [{ id: 84811 }];
      jest.spyOn(activityService, 'query').mockReturnValue(of(new HttpResponse({ body: activityCollection })));
      const additionalActivities = [...activities];
      const expectedCollection: IActivity[] = [...additionalActivities, ...activityCollection];
      jest.spyOn(activityService, 'addActivityToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ staff });
      comp.ngOnInit();

      expect(activityService.query).toHaveBeenCalled();
      expect(activityService.addActivityToCollectionIfMissing).toHaveBeenCalledWith(
        activityCollection,
        ...additionalActivities.map(expect.objectContaining)
      );
      expect(comp.activitiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const staff: IStaff = { id: 456 };
      const prison: IPrison = { id: 64462 };
      staff.prison = prison;
      const activity: IActivity = { id: 39830 };
      staff.activities = [activity];

      activatedRoute.data = of({ staff });
      comp.ngOnInit();

      expect(comp.prisonsSharedCollection).toContain(prison);
      expect(comp.activitiesSharedCollection).toContain(activity);
      expect(comp.staff).toEqual(staff);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStaff>>();
      const staff = { id: 123 };
      jest.spyOn(staffFormService, 'getStaff').mockReturnValue(staff);
      jest.spyOn(staffService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ staff });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: staff }));
      saveSubject.complete();

      // THEN
      expect(staffFormService.getStaff).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(staffService.update).toHaveBeenCalledWith(expect.objectContaining(staff));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStaff>>();
      const staff = { id: 123 };
      jest.spyOn(staffFormService, 'getStaff').mockReturnValue({ id: null });
      jest.spyOn(staffService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ staff: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: staff }));
      saveSubject.complete();

      // THEN
      expect(staffFormService.getStaff).toHaveBeenCalled();
      expect(staffService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStaff>>();
      const staff = { id: 123 };
      jest.spyOn(staffService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ staff });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(staffService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePrison', () => {
      it('Should forward to prisonService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(prisonService, 'comparePrison');
        comp.comparePrison(entity, entity2);
        expect(prisonService.comparePrison).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareActivity', () => {
      it('Should forward to activityService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(activityService, 'compareActivity');
        comp.compareActivity(entity, entity2);
        expect(activityService.compareActivity).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
