import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IStaff, NewStaff } from '../staff.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStaff for edit and NewStaffFormGroupInput for create.
 */
type StaffFormGroupInput = IStaff | PartialWithRequiredKeyOf<NewStaff>;

type StaffFormDefaults = Pick<NewStaff, 'id' | 'activities' | 'assignedAreas'>;

type StaffFormGroupContent = {
  id: FormControl<IStaff['id'] | NewStaff['id']>;
  staffType: FormControl<IStaff['staffType']>;
  firstName: FormControl<IStaff['firstName']>;
  lastName: FormControl<IStaff['lastName']>;
  prison: FormControl<IStaff['prison']>;
  activities: FormControl<IStaff['activities']>;
  assignedAreas: FormControl<IStaff['assignedAreas']>;
};

export type StaffFormGroup = FormGroup<StaffFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StaffFormService {
  createStaffFormGroup(staff: StaffFormGroupInput = { id: null }): StaffFormGroup {
    const staffRawValue = {
      ...this.getFormDefaults(),
      ...staff,
    };
    return new FormGroup<StaffFormGroupContent>({
      id: new FormControl(
        { value: staffRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      staffType: new FormControl(staffRawValue.staffType),
      firstName: new FormControl(staffRawValue.firstName),
      lastName: new FormControl(staffRawValue.lastName),
      prison: new FormControl(staffRawValue.prison),
      activities: new FormControl(staffRawValue.activities ?? []),
      assignedAreas: new FormControl(staffRawValue.assignedAreas ?? []),
    });
  }

  getStaff(form: StaffFormGroup): IStaff | NewStaff {
    return form.getRawValue() as IStaff | NewStaff;
  }

  resetForm(form: StaffFormGroup, staff: StaffFormGroupInput): void {
    const staffRawValue = { ...this.getFormDefaults(), ...staff };
    form.reset(
      {
        ...staffRawValue,
        id: { value: staffRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): StaffFormDefaults {
    return {
      id: null,
      activities: [],
      assignedAreas: [],
    };
  }
}
