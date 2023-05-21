import dayjs from 'dayjs/esm';

import { IInmate, NewInmate } from './inmate.model';

export const sampleWithRequiredData: IInmate = {
  id: 29620,
};

export const sampleWithPartialData: IInmate = {
  id: 19844,
  firstName: 'Rocio',
  dateOfBirth: dayjs('2023-05-19'),
  dateOfIncarceration: dayjs('2023-05-20'),
};

export const sampleWithFullData: IInmate = {
  id: 62769,
  firstName: 'Davonte',
  lastName: 'Bode',
  dateOfBirth: dayjs('2023-05-20'),
  dateOfIncarceration: dayjs('2023-05-20'),
  dateOfExpectedRelease: dayjs('2023-05-20'),
};

export const sampleWithNewData: NewInmate = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
