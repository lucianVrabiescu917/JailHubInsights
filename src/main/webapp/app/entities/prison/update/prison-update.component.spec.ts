import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PrisonFormService } from './prison-form.service';
import { PrisonService } from '../service/prison.service';
import { IPrison } from '../prison.model';

import { PrisonUpdateComponent } from './prison-update.component';

describe('Prison Management Update Component', () => {
  let comp: PrisonUpdateComponent;
  let fixture: ComponentFixture<PrisonUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let prisonFormService: PrisonFormService;
  let prisonService: PrisonService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PrisonUpdateComponent],
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
      .overrideTemplate(PrisonUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PrisonUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    prisonFormService = TestBed.inject(PrisonFormService);
    prisonService = TestBed.inject(PrisonService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const prison: IPrison = { id: 456 };

      activatedRoute.data = of({ prison });
      comp.ngOnInit();

      expect(comp.prison).toEqual(prison);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPrison>>();
      const prison = { id: 123 };
      jest.spyOn(prisonFormService, 'getPrison').mockReturnValue(prison);
      jest.spyOn(prisonService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ prison });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: prison }));
      saveSubject.complete();

      // THEN
      expect(prisonFormService.getPrison).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(prisonService.update).toHaveBeenCalledWith(expect.objectContaining(prison));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPrison>>();
      const prison = { id: 123 };
      jest.spyOn(prisonFormService, 'getPrison').mockReturnValue({ id: null });
      jest.spyOn(prisonService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ prison: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: prison }));
      saveSubject.complete();

      // THEN
      expect(prisonFormService.getPrison).toHaveBeenCalled();
      expect(prisonService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPrison>>();
      const prison = { id: 123 };
      jest.spyOn(prisonService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ prison });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(prisonService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
