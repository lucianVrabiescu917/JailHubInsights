import { IPrison } from 'app/entities/prison/prison.model';
import { IActivity } from 'app/entities/activity/activity.model';
import { IArea } from 'app/entities/area/area.model';
import { StaffType } from 'app/entities/enumerations/staff-type.model';

export interface IStaff {
  id: number;
  staffType?: StaffType | null;
  firstName?: string | null;
  lastName?: string | null;
  prison?: IPrison | null;
  assignedAreas?: IArea[] | null;
}

export type NewStaff = Omit<IStaff, 'id'> & { id: null };
