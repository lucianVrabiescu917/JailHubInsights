import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IArea, NewArea } from '../area.model';

export type PartialUpdateArea = Partial<IArea> & Pick<IArea, 'id'>;

export type EntityResponseType = HttpResponse<IArea>;
export type EntityArrayResponseType = HttpResponse<IArea[]>;

@Injectable({ providedIn: 'root' })
export class AreaService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/areas');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(area: NewArea): Observable<EntityResponseType> {
    return this.http.post<IArea>(this.resourceUrl, area, { observe: 'response' });
  }

  update(area: IArea): Observable<EntityResponseType> {
    return this.http.put<IArea>(`${this.resourceUrl}/${this.getAreaIdentifier(area)}`, area, { observe: 'response' });
  }

  partialUpdate(area: PartialUpdateArea): Observable<EntityResponseType> {
    return this.http.patch<IArea>(`${this.resourceUrl}/${this.getAreaIdentifier(area)}`, area, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IArea>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IArea[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAreaIdentifier(area: Pick<IArea, 'id'>): number {
    return area.id;
  }

  compareArea(o1: Pick<IArea, 'id'> | null, o2: Pick<IArea, 'id'> | null): boolean {
    return o1 && o2 ? this.getAreaIdentifier(o1) === this.getAreaIdentifier(o2) : o1 === o2;
  }

  addAreaToCollectionIfMissing<Type extends Pick<IArea, 'id'>>(
    areaCollection: Type[],
    ...areasToCheck: (Type | null | undefined)[]
  ): Type[] {
    const areas: Type[] = areasToCheck.filter(isPresent);
    if (areas.length > 0) {
      const areaCollectionIdentifiers = areaCollection.map(areaItem => this.getAreaIdentifier(areaItem)!);
      const areasToAdd = areas.filter(areaItem => {
        const areaIdentifier = this.getAreaIdentifier(areaItem);
        if (areaCollectionIdentifiers.includes(areaIdentifier)) {
          return false;
        }
        areaCollectionIdentifiers.push(areaIdentifier);
        return true;
      });
      return [...areasToAdd, ...areaCollection];
    }
    return areaCollection;
  }
}
