import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../inmate.test-samples';

import { InmateFormService } from './inmate-form.service';

describe('Inmate Form Service', () => {
  let service: InmateFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InmateFormService);
  });

  describe('Service methods', () => {
    describe('createInmateFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createInmateFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            dateOfBirth: expect.any(Object),
            dateOfIncarceration: expect.any(Object),
            dateOfExpectedRelease: expect.any(Object),
            prison: expect.any(Object),
            assignedCell: expect.any(Object),
            activities: expect.any(Object),
          })
        );
      });

      it('passing IInmate should create a new form with FormGroup', () => {
        const formGroup = service.createInmateFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            dateOfBirth: expect.any(Object),
            dateOfIncarceration: expect.any(Object),
            dateOfExpectedRelease: expect.any(Object),
            prison: expect.any(Object),
            assignedCell: expect.any(Object),
            activities: expect.any(Object),
          })
        );
      });
    });

    describe('getInmate', () => {
      it('should return NewInmate for default Inmate initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createInmateFormGroup(sampleWithNewData);

        const inmate = service.getInmate(formGroup) as any;

        expect(inmate).toMatchObject(sampleWithNewData);
      });

      it('should return NewInmate for empty Inmate initial value', () => {
        const formGroup = service.createInmateFormGroup();

        const inmate = service.getInmate(formGroup) as any;

        expect(inmate).toMatchObject({});
      });

      it('should return IInmate', () => {
        const formGroup = service.createInmateFormGroup(sampleWithRequiredData);

        const inmate = service.getInmate(formGroup) as any;

        expect(inmate).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IInmate should not enable id FormControl', () => {
        const formGroup = service.createInmateFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewInmate should disable id FormControl', () => {
        const formGroup = service.createInmateFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
