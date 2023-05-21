import { ActivityType } from 'app/entities/enumerations/activity-type.model';

import { IActivity, NewActivity } from './activity.model';

export const sampleWithRequiredData: IActivity = {
  id: 68021,
};

export const sampleWithPartialData: IActivity = {
  id: 52009,
  type: ActivityType['REHABILITATION'],
  title: 'pixel Industrial Buckinghamshire',
};

export const sampleWithFullData: IActivity = {
  id: 69975,
  type: ActivityType['LABOR'],
  title: 'Product programming open-source',
  description: 'platforms Branch',
};

export const sampleWithNewData: NewActivity = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
