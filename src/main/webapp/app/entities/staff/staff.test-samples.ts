import { StaffType } from 'app/entities/enumerations/staff-type.model';

import { IStaff, NewStaff } from './staff.model';

export const sampleWithRequiredData: IStaff = {
  id: 58821,
};

export const sampleWithPartialData: IStaff = {
  id: 71350,
  firstName: 'Henry',
  lastName: 'Wunsch',
};

export const sampleWithFullData: IStaff = {
  id: 52052,
  staffType: StaffType['GUARD'],
  firstName: 'Meredith',
  lastName: 'Mosciski',
};

export const sampleWithNewData: NewStaff = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
