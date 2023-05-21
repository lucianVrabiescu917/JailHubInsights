import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ActivityFormService } from './activity-form.service';
import { ActivityService } from '../service/activity.service';
import { IActivity } from '../activity.model';
import { IPrison } from 'app/entities/prison/prison.model';
import { PrisonService } from 'app/entities/prison/service/prison.service';

import { ActivityUpdateComponent } from './activity-update.component';

describe('Activity Management Update Component', () => {
  let comp: ActivityUpdateComponent;
  let fixture: ComponentFixture<ActivityUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let activityFormService: ActivityFormService;
  let activityService: ActivityService;
  let prisonService: PrisonService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ActivityUpdateComponent],
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
      .overrideTemplate(ActivityUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ActivityUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    activityFormService = TestBed.inject(ActivityFormService);
    activityService = TestBed.inject(ActivityService);
    prisonService = TestBed.inject(PrisonService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Prison query and add missing value', () => {
      const activity: IActivity = { id: 456 };
      const prison: IPrison = { id: 11024 };
      activity.prison = prison;

      const prisonCollection: IPrison[] = [{ id: 20075 }];
      jest.spyOn(prisonService, 'query').mockReturnValue(of(new HttpResponse({ body: prisonCollection })));
      const additionalPrisons = [prison];
      const expectedCollection: IPrison[] = [...additionalPrisons, ...prisonCollection];
      jest.spyOn(prisonService, 'addPrisonToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ activity });
      comp.ngOnInit();

      expect(prisonService.query).toHaveBeenCalled();
      expect(prisonService.addPrisonToCollectionIfMissing).toHaveBeenCalledWith(
        prisonCollection,
        ...additionalPrisons.map(expect.objectContaining)
      );
      expect(comp.prisonsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const activity: IActivity = { id: 456 };
      const prison: IPrison = { id: 15130 };
      activity.prison = prison;

      activatedRoute.data = of({ activity });
      comp.ngOnInit();

      expect(comp.prisonsSharedCollection).toContain(prison);
      expect(comp.activity).toEqual(activity);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IActivity>>();
      const activity = { id: 123 };
      jest.spyOn(activityFormService, 'getActivity').mockReturnValue(activity);
      jest.spyOn(activityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ activity });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: activity }));
      saveSubject.complete();

      // THEN
      expect(activityFormService.getActivity).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(activityService.update).toHaveBeenCalledWith(expect.objectContaining(activity));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IActivity>>();
      const activity = { id: 123 };
      jest.spyOn(activityFormService, 'getActivity').mockReturnValue({ id: null });
      jest.spyOn(activityService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ activity: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: activity }));
      saveSubject.complete();

      // THEN
      expect(activityFormService.getActivity).toHaveBeenCalled();
      expect(activityService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IActivity>>();
      const activity = { id: 123 };
      jest.spyOn(activityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ activity });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(activityService.update).toHaveBeenCalled();
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
  });
});
