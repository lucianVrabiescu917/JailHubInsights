import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AreaFormService } from './area-form.service';
import { AreaService } from '../service/area.service';
import { IArea } from '../area.model';
import { IPrison } from 'app/entities/prison/prison.model';
import { PrisonService } from 'app/entities/prison/service/prison.service';
import { IStaff } from 'app/entities/staff/staff.model';
import { StaffService } from 'app/entities/staff/service/staff.service';

import { AreaUpdateComponent } from './area-update.component';

describe('Area Management Update Component', () => {
  let comp: AreaUpdateComponent;
  let fixture: ComponentFixture<AreaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let areaFormService: AreaFormService;
  let areaService: AreaService;
  let prisonService: PrisonService;
  let staffService: StaffService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AreaUpdateComponent],
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
      .overrideTemplate(AreaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AreaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    areaFormService = TestBed.inject(AreaFormService);
    areaService = TestBed.inject(AreaService);
    prisonService = TestBed.inject(PrisonService);
    staffService = TestBed.inject(StaffService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Prison query and add missing value', () => {
      const area: IArea = { id: 456 };
      const prison: IPrison = { id: 61954 };
      area.prison = prison;

      const prisonCollection: IPrison[] = [{ id: 54252 }];
      jest.spyOn(prisonService, 'query').mockReturnValue(of(new HttpResponse({ body: prisonCollection })));
      const additionalPrisons = [prison];
      const expectedCollection: IPrison[] = [...additionalPrisons, ...prisonCollection];
      jest.spyOn(prisonService, 'addPrisonToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ area });
      comp.ngOnInit();

      expect(prisonService.query).toHaveBeenCalled();
      expect(prisonService.addPrisonToCollectionIfMissing).toHaveBeenCalledWith(
        prisonCollection,
        ...additionalPrisons.map(expect.objectContaining)
      );
      expect(comp.prisonsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Staff query and add missing value', () => {
      const area: IArea = { id: 456 };
      const assignedStaffAreas: IStaff[] = [{ id: 80133 }];
      area.assignedStaffAreas = assignedStaffAreas;

      const staffCollection: IStaff[] = [{ id: 24901 }];
      jest.spyOn(staffService, 'query').mockReturnValue(of(new HttpResponse({ body: staffCollection })));
      const additionalStaff = [...assignedStaffAreas];
      const expectedCollection: IStaff[] = [...additionalStaff, ...staffCollection];
      jest.spyOn(staffService, 'addStaffToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ area });
      comp.ngOnInit();

      expect(staffService.query).toHaveBeenCalled();
      expect(staffService.addStaffToCollectionIfMissing).toHaveBeenCalledWith(
        staffCollection,
        ...additionalStaff.map(expect.objectContaining)
      );
      expect(comp.staffSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Area query and add missing value', () => {
      const area: IArea = { id: 456 };
      const composedOfAreas: IArea[] = [{ id: 66723 }];
      area.composedOfAreas = composedOfAreas;

      const areaCollection: IArea[] = [{ id: 60247 }];
      jest.spyOn(areaService, 'query').mockReturnValue(of(new HttpResponse({ body: areaCollection })));
      const additionalAreas = [...composedOfAreas];
      const expectedCollection: IArea[] = [...additionalAreas, ...areaCollection];
      jest.spyOn(areaService, 'addAreaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ area });
      comp.ngOnInit();

      expect(areaService.query).toHaveBeenCalled();
      expect(areaService.addAreaToCollectionIfMissing).toHaveBeenCalledWith(
        areaCollection,
        ...additionalAreas.map(expect.objectContaining)
      );
      expect(comp.areasSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const area: IArea = { id: 456 };
      const prison: IPrison = { id: 76752 };
      area.prison = prison;
      const assignedStaffAreas: IStaff = { id: 8318 };
      area.assignedStaffAreas = [assignedStaffAreas];
      const composedOfAreas: IArea = { id: 5367 };
      area.composedOfAreas = [composedOfAreas];

      activatedRoute.data = of({ area });
      comp.ngOnInit();

      expect(comp.prisonsSharedCollection).toContain(prison);
      expect(comp.staffSharedCollection).toContain(assignedStaffAreas);
      expect(comp.areasSharedCollection).toContain(composedOfAreas);
      expect(comp.area).toEqual(area);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IArea>>();
      const area = { id: 123 };
      jest.spyOn(areaFormService, 'getArea').mockReturnValue(area);
      jest.spyOn(areaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ area });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: area }));
      saveSubject.complete();

      // THEN
      expect(areaFormService.getArea).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(areaService.update).toHaveBeenCalledWith(expect.objectContaining(area));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IArea>>();
      const area = { id: 123 };
      jest.spyOn(areaFormService, 'getArea').mockReturnValue({ id: null });
      jest.spyOn(areaService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ area: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: area }));
      saveSubject.complete();

      // THEN
      expect(areaFormService.getArea).toHaveBeenCalled();
      expect(areaService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IArea>>();
      const area = { id: 123 };
      jest.spyOn(areaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ area });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(areaService.update).toHaveBeenCalled();
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

    describe('compareStaff', () => {
      it('Should forward to staffService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(staffService, 'compareStaff');
        comp.compareStaff(entity, entity2);
        expect(staffService.compareStaff).toHaveBeenCalledWith(entity, entity2);
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
  });
});
