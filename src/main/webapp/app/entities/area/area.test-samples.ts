import { AreaType } from 'app/entities/enumerations/area-type.model';

import { IArea, NewArea } from './area.model';

export const sampleWithRequiredData: IArea = {
  id: 11565,
};

export const sampleWithPartialData: IArea = {
  id: 33154,
};

export const sampleWithFullData: IArea = {
  id: 54825,
  name: 'Louisiana',
  areaSize: 42175,
  areaType: AreaType['CLASS'],
};

export const sampleWithNewData: NewArea = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
