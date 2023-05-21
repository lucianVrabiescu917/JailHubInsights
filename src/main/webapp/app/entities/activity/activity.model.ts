import { IPrison } from 'app/entities/prison/prison.model';
import { IInmate } from 'app/entities/inmate/inmate.model';
import { IStaff } from 'app/entities/staff/staff.model';
import { ActivityType } from 'app/entities/enumerations/activity-type.model';

export interface IActivity {
  id: number;
  type?: ActivityType | null;
  title?: string | null;
  description?: string | null;
  prison?: Pick<IPrison, 'id'> | null;
  inmates?: Pick<IInmate, 'id'>[] | null;
  staff?: Pick<IStaff, 'id'>[] | null;
}

export type NewActivity = Omit<IActivity, 'id'> & { id: null };
