import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IArea, NewArea } from '../area.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IArea for edit and NewAreaFormGroupInput for create.
 */
type AreaFormGroupInput = IArea | PartialWithRequiredKeyOf<NewArea>;

type AreaFormDefaults = Pick<NewArea, 'id' | 'assignedStaffAreas' | 'composedOfAreas' | 'composingAreas'>;

type AreaFormGroupContent = {
  id: FormControl<IArea['id'] | NewArea['id']>;
  name: FormControl<IArea['name']>;
  areaSize: FormControl<IArea['areaSize']>;
  areaType: FormControl<IArea['areaType']>;
  prison: FormControl<IArea['prison']>;
  assignedStaffAreas: FormControl<IArea['assignedStaffAreas']>;
  composedOfAreas: FormControl<IArea['composedOfAreas']>;
  composingAreas: FormControl<IArea['composingAreas']>;
};

export type AreaFormGroup = FormGroup<AreaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AreaFormService {
  createAreaFormGroup(area: AreaFormGroupInput = { id: null }): AreaFormGroup {
    const areaRawValue = {
      ...this.getFormDefaults(),
      ...area,
    };
    return new FormGroup<AreaFormGroupContent>({
      id: new FormControl(
        { value: areaRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(areaRawValue.name),
      areaSize: new FormControl(areaRawValue.areaSize),
      areaType: new FormControl(areaRawValue.areaType),
      prison: new FormControl(areaRawValue.prison),
      assignedStaffAreas: new FormControl(areaRawValue.assignedStaffAreas ?? []),
      composedOfAreas: new FormControl(areaRawValue.composedOfAreas ?? []),
      composingAreas: new FormControl(areaRawValue.composingAreas ?? []),
    });
  }

  getArea(form: AreaFormGroup): IArea | NewArea {
    return form.getRawValue() as IArea | NewArea;
  }

  resetForm(form: AreaFormGroup, area: AreaFormGroupInput): void {
    const areaRawValue = { ...this.getFormDefaults(), ...area };
    form.reset(
      {
        ...areaRawValue,
        id: { value: areaRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): AreaFormDefaults {
    return {
      id: null,
      assignedStaffAreas: [],
      composedOfAreas: [],
      composingAreas: [],
    };
  }
}
