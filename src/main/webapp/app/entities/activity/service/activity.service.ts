import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IActivity, NewActivity } from '../activity.model';

export type PartialUpdateActivity = Partial<IActivity> & Pick<IActivity, 'id'>;

export type EntityResponseType = HttpResponse<IActivity>;
export type EntityArrayResponseType = HttpResponse<IActivity[]>;

@Injectable({ providedIn: 'root' })
export class ActivityService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/activities');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(activity: NewActivity): Observable<EntityResponseType> {
    return this.http.post<IActivity>(this.resourceUrl, activity, { observe: 'response' });
  }

  update(activity: IActivity): Observable<EntityResponseType> {
    return this.http.put<IActivity>(`${this.resourceUrl}/${this.getActivityIdentifier(activity)}`, activity, { observe: 'response' });
  }

  partialUpdate(activity: PartialUpdateActivity): Observable<EntityResponseType> {
    return this.http.patch<IActivity>(`${this.resourceUrl}/${this.getActivityIdentifier(activity)}`, activity, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IActivity>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IActivity[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  querySearch(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IActivity[]>(`${this.resourceUrl}/hint`, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getActivityIdentifier(activity: Pick<IActivity, 'id'>): number {
    return activity.id;
  }

  compareActivity(o1: Pick<IActivity, 'id'> | null, o2: Pick<IActivity, 'id'> | null): boolean {
    return o1 && o2 ? this.getActivityIdentifier(o1) === this.getActivityIdentifier(o2) : o1 === o2;
  }

  addActivityToCollectionIfMissing<Type extends Pick<IActivity, 'id'>>(
    activityCollection: Type[],
    ...activitiesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const activities: Type[] = activitiesToCheck.filter(isPresent);
    if (activities.length > 0) {
      const activityCollectionIdentifiers = activityCollection.map(activityItem => this.getActivityIdentifier(activityItem)!);
      const activitiesToAdd = activities.filter(activityItem => {
        const activityIdentifier = this.getActivityIdentifier(activityItem);
        if (activityCollectionIdentifiers.includes(activityIdentifier)) {
          return false;
        }
        activityCollectionIdentifiers.push(activityIdentifier);
        return true;
      });
      return [...activitiesToAdd, ...activityCollection];
    }
    return activityCollection;
  }
}
