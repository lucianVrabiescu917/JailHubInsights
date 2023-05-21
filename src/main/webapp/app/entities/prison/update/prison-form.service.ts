import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPrison, NewPrison } from '../prison.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPrison for edit and NewPrisonFormGroupInput for create.
 */
type PrisonFormGroupInput = IPrison | PartialWithRequiredKeyOf<NewPrison>;

type PrisonFormDefaults = Pick<NewPrison, 'id'>;

type PrisonFormGroupContent = {
  id: FormControl<IPrison['id'] | NewPrison['id']>;
  name: FormControl<IPrison['name']>;
  location: FormControl<IPrison['location']>;
};

export type PrisonFormGroup = FormGroup<PrisonFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PrisonFormService {
  createPrisonFormGroup(prison: PrisonFormGroupInput = { id: null }): PrisonFormGroup {
    const prisonRawValue = {
      ...this.getFormDefaults(),
      ...prison,
    };
    return new FormGroup<PrisonFormGroupContent>({
      id: new FormControl(
        { value: prisonRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(prisonRawValue.name),
      location: new FormControl(prisonRawValue.location),
    });
  }

  getPrison(form: PrisonFormGroup): IPrison | NewPrison {
    return form.getRawValue() as IPrison | NewPrison;
  }

  resetForm(form: PrisonFormGroup, prison: PrisonFormGroupInput): void {
    const prisonRawValue = { ...this.getFormDefaults(), ...prison };
    form.reset(
      {
        ...prisonRawValue,
        id: { value: prisonRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): PrisonFormDefaults {
    return {
      id: null,
    };
  }
}
