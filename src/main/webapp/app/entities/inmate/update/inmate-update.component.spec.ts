import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { InmateFormService } from './inmate-form.service';
import { InmateService } from '../service/inmate.service';
import { IInmate } from '../inmate.model';
import { IPrison } from 'app/entities/prison/prison.model';
import { PrisonService } from 'app/entities/prison/service/prison.service';
import { IArea } from 'app/entities/area/area.model';
import { AreaService } from 'app/entities/area/service/area.service';
import { IActivity } from 'app/entities/activity/activity.model';
import { ActivityService } from 'app/entities/activity/service/activity.service';

import { InmateUpdateComponent } from './inmate-update.component';

describe('Inmate Management Update Component', () => {
  let comp: InmateUpdateComponent;
  let fixture: ComponentFixture<InmateUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let inmateFormService: InmateFormService;
  let inmateService: InmateService;
  let prisonService: PrisonService;
  let areaService: AreaService;
  let activityService: ActivityService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [InmateUpdateComponent],
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
      .overrideTemplate(InmateUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(InmateUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    inmateFormService = TestBed.inject(InmateFormService);
    inmateService = TestBed.inject(InmateService);
    prisonService = TestBed.inject(PrisonService);
    areaService = TestBed.inject(AreaService);
    activityService = TestBed.inject(ActivityService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Prison query and add missing value', () => {
      const inmate: IInmate = { id: 456 };
      const prison: IPrison = { id: 81422 };
      inmate.prison = prison;

      const prisonCollection: IPrison[] = [{ id: 71966 }];
      jest.spyOn(prisonService, 'query').mockReturnValue(of(new HttpResponse({ body: prisonCollection })));
      const additionalPrisons = [prison];
      const expectedCollection: IPrison[] = [...additionalPrisons, ...prisonCollection];
      jest.spyOn(prisonService, 'addPrisonToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ inmate });
      comp.ngOnInit();

      expect(prisonService.query).toHaveBeenCalled();
      expect(prisonService.addPrisonToCollectionIfMissing).toHaveBeenCalledWith(
        prisonCollection,
        ...additionalPrisons.map(expect.objectContaining)
      );
      expect(comp.prisonsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Area query and add missing value', () => {
      const inmate: IInmate = { id: 456 };
      const assignedCell: IArea = { id: 420 };
      inmate.assignedCell = assignedCell;

      const areaCollection: IArea[] = [{ id: 36709 }];
      jest.spyOn(areaService, 'query').mockReturnValue(of(new HttpResponse({ body: areaCollection })));
      const additionalAreas = [assignedCell];
      const expectedCollection: IArea[] = [...additionalAreas, ...areaCollection];
      jest.spyOn(areaService, 'addAreaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ inmate });
      comp.ngOnInit();

      expect(areaService.query).toHaveBeenCalled();
      expect(areaService.addAreaToCollectionIfMissing).toHaveBeenCalledWith(
        areaCollection,
        ...additionalAreas.map(expect.objectContaining)
      );
      expect(comp.areasSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Activity query and add missing value', () => {
      const inmate: IInmate = { id: 456 };
      const activities: IActivity[] = [{ id: 24719 }];
      inmate.activities = activities;

      const activityCollection: IActivity[] = [{ id: 15707 }];
      jest.spyOn(activityService, 'query').mockReturnValue(of(new HttpResponse({ body: activityCollection })));
      const additionalActivities = [...activities];
      const expectedCollection: IActivity[] = [...additionalActivities, ...activityCollection];
      jest.spyOn(activityService, 'addActivityToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ inmate });
      comp.ngOnInit();

      expect(activityService.query).toHaveBeenCalled();
      expect(activityService.addActivityToCollectionIfMissing).toHaveBeenCalledWith(
        activityCollection,
        ...additionalActivities.map(expect.objectContaining)
      );
      expect(comp.activitiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const inmate: IInmate = { id: 456 };
      const prison: IPrison = { id: 63331 };
      inmate.prison = prison;
      const assignedCell: IArea = { id: 21184 };
      inmate.assignedCell = assignedCell;
      const activity: IActivity = { id: 52532 };
      inmate.activities = [activity];

      activatedRoute.data = of({ inmate });
      comp.ngOnInit();

      expect(comp.prisonsSharedCollection).toContain(prison);
      expect(comp.areasSharedCollection).toContain(assignedCell);
      expect(comp.activitiesSharedCollection).toContain(activity);
      expect(comp.inmate).toEqual(inmate);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInmate>>();
      const inmate = { id: 123 };
      jest.spyOn(inmateFormService, 'getInmate').mockReturnValue(inmate);
      jest.spyOn(inmateService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inmate });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: inmate }));
      saveSubject.complete();

      // THEN
      expect(inmateFormService.getInmate).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(inmateService.update).toHaveBeenCalledWith(expect.objectContaining(inmate));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInmate>>();
      const inmate = { id: 123 };
      jest.spyOn(inmateFormService, 'getInmate').mockReturnValue({ id: null });
      jest.spyOn(inmateService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inmate: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: inmate }));
      saveSubject.complete();

      // THEN
      expect(inmateFormService.getInmate).toHaveBeenCalled();
      expect(inmateService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInmate>>();
      const inmate = { id: 123 };
      jest.spyOn(inmateService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inmate });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(inmateService.update).toHaveBeenCalled();
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

    describe('compareArea', () => {
      it('Should forward to areaService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(areaService, 'compareArea');
        comp.compareArea(entity, entity2);
        expect(areaService.compareArea).toHaveBeenCalledWith(entity, entity2);
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
