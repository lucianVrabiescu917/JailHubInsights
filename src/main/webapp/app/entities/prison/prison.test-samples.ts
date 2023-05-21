import { IPrison, NewPrison } from './prison.model';

export const sampleWithRequiredData: IPrison = {
  id: 89536,
};

export const sampleWithPartialData: IPrison = {
  id: 15652,
  name: 'programming Berkshire',
};

export const sampleWithFullData: IPrison = {
  id: 16777,
  name: 'compress Assistant',
  location: 'holistic Somoni Internal',
};

export const sampleWithNewData: NewPrison = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
