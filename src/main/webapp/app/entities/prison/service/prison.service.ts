import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPrison, NewPrison } from '../prison.model';

export type PartialUpdatePrison = Partial<IPrison> & Pick<IPrison, 'id'>;

export type EntityResponseType = HttpResponse<IPrison>;
export type EntityArrayResponseType = HttpResponse<IPrison[]>;

@Injectable({ providedIn: 'root' })
export class PrisonService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/prisons');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(prison: NewPrison): Observable<EntityResponseType> {
    return this.http.post<IPrison>(this.resourceUrl, prison, { observe: 'response' });
  }

  update(prison: IPrison): Observable<EntityResponseType> {
    return this.http.put<IPrison>(`${this.resourceUrl}/${this.getPrisonIdentifier(prison)}`, prison, { observe: 'response' });
  }

  partialUpdate(prison: PartialUpdatePrison): Observable<EntityResponseType> {
    return this.http.patch<IPrison>(`${this.resourceUrl}/${this.getPrisonIdentifier(prison)}`, prison, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPrison>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPrison[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPrisonIdentifier(prison: Pick<IPrison, 'id'>): number {
    return prison.id;
  }

  comparePrison(o1: Pick<IPrison, 'id'> | null, o2: Pick<IPrison, 'id'> | null): boolean {
    return o1 && o2 ? this.getPrisonIdentifier(o1) === this.getPrisonIdentifier(o2) : o1 === o2;
  }

  addPrisonToCollectionIfMissing<Type extends Pick<IPrison, 'id'>>(
    prisonCollection: Type[],
    ...prisonsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const prisons: Type[] = prisonsToCheck.filter(isPresent);
    if (prisons.length > 0) {
      const prisonCollectionIdentifiers = prisonCollection.map(prisonItem => this.getPrisonIdentifier(prisonItem)!);
      const prisonsToAdd = prisons.filter(prisonItem => {
        const prisonIdentifier = this.getPrisonIdentifier(prisonItem);
        if (prisonCollectionIdentifiers.includes(prisonIdentifier)) {
          return false;
        }
        prisonCollectionIdentifiers.push(prisonIdentifier);
        return true;
      });
      return [...prisonsToAdd, ...prisonCollection];
    }
    return prisonCollection;
  }
}
