import { IPrison } from 'app/entities/prison/prison.model';
import { IStaff } from 'app/entities/staff/staff.model';
import { AreaType } from 'app/entities/enumerations/area-type.model';
import { IInmate } from '../inmate/inmate.model';

export interface IArea {
  id: number;
  name?: string | null;
  areaSize?: number | null;
  areaType?: AreaType | null;
  prison?: IPrison | null;
  assignedStaffAreas?: IStaff[] | null;
  inmates?: IInmate[] | null;
  composedOfAreas?: IArea[] | null;
  composingAreas?: IArea[] | null;
}

export type NewArea = Omit<IArea, 'id'> & { id: null };
