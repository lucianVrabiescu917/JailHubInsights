import dayjs from 'dayjs/esm';
import { IPrison } from 'app/entities/prison/prison.model';
import { IArea } from 'app/entities/area/area.model';
import { IActivity } from 'app/entities/activity/activity.model';

export interface IInmate {
  id: number;
  firstName?: string | null;
  lastName?: string | null;
  dateOfBirth?: dayjs.Dayjs | null;
  dateOfIncarceration?: dayjs.Dayjs | null;
  dateOfExpectedRelease?: dayjs.Dayjs | null;
  prison?: IPrison | null;
  assignedCell?: IArea | null;
  activities?: IActivity[] | null;
  image?: string | null;
  assignedAreas?: IArea[] | null;
}

export type NewInmate = Omit<IInmate, 'id'> & { id: null };
