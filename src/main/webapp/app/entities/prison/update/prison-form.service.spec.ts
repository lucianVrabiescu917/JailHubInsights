import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../prison.test-samples';

import { PrisonFormService } from './prison-form.service';

describe('Prison Form Service', () => {
  let service: PrisonFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PrisonFormService);
  });

  describe('Service methods', () => {
    describe('createPrisonFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPrisonFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            location: expect.any(Object),
          })
        );
      });

      it('passing IPrison should create a new form with FormGroup', () => {
        const formGroup = service.createPrisonFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            location: expect.any(Object),
          })
        );
      });
    });

    describe('getPrison', () => {
      it('should return NewPrison for default Prison initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createPrisonFormGroup(sampleWithNewData);

        const prison = service.getPrison(formGroup) as any;

        expect(prison).toMatchObject(sampleWithNewData);
      });

      it('should return NewPrison for empty Prison initial value', () => {
        const formGroup = service.createPrisonFormGroup();

        const prison = service.getPrison(formGroup) as any;

        expect(prison).toMatchObject({});
      });

      it('should return IPrison', () => {
        const formGroup = service.createPrisonFormGroup(sampleWithRequiredData);

        const prison = service.getPrison(formGroup) as any;

        expect(prison).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPrison should not enable id FormControl', () => {
        const formGroup = service.createPrisonFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPrison should disable id FormControl', () => {
        const formGroup = service.createPrisonFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
