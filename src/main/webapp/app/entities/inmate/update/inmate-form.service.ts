import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IInmate, NewInmate } from '../inmate.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IInmate for edit and NewInmateFormGroupInput for create.
 */
type InmateFormGroupInput = IInmate | PartialWithRequiredKeyOf<NewInmate>;

type InmateFormDefaults = Pick<NewInmate, 'id' | 'activities'>;

type InmateFormGroupContent = {
  id: FormControl<IInmate['id'] | NewInmate['id']>;
  firstName: FormControl<IInmate['firstName']>;
  lastName: FormControl<IInmate['lastName']>;
  dateOfBirth: FormControl<IInmate['dateOfBirth']>;
  dateOfIncarceration: FormControl<IInmate['dateOfIncarceration']>;
  dateOfExpectedRelease: FormControl<IInmate['dateOfExpectedRelease']>;
  prison: FormControl<IInmate['prison']>;
  assignedCell: FormControl<IInmate['assignedCell']>;
  activities: FormControl<IInmate['activities']>;
};

export type InmateFormGroup = FormGroup<InmateFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class InmateFormService {
  createInmateFormGroup(inmate: InmateFormGroupInput = { id: null }): InmateFormGroup {
    const inmateRawValue = {
      ...this.getFormDefaults(),
      ...inmate,
    };
    return new FormGroup<InmateFormGroupContent>({
      id: new FormControl(
        { value: inmateRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      firstName: new FormControl(inmateRawValue.firstName),
      lastName: new FormControl(inmateRawValue.lastName),
      dateOfBirth: new FormControl(inmateRawValue.dateOfBirth),
      dateOfIncarceration: new FormControl(inmateRawValue.dateOfIncarceration),
      dateOfExpectedRelease: new FormControl(inmateRawValue.dateOfExpectedRelease),
      prison: new FormControl(inmateRawValue.prison),
      assignedCell: new FormControl(inmateRawValue.assignedCell),
      activities: new FormControl(inmateRawValue.activities ?? []),
    });
  }

  getInmate(form: InmateFormGroup): IInmate | NewInmate {
    return form.getRawValue() as IInmate | NewInmate;
  }

  resetForm(form: InmateFormGroup, inmate: InmateFormGroupInput): void {
    const inmateRawValue = { ...this.getFormDefaults(), ...inmate };
    form.reset(
      {
        ...inmateRawValue,
        id: { value: inmateRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): InmateFormDefaults {
    return {
      id: null,
      activities: [],
    };
  }
}
