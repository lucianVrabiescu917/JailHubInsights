import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IInmate, NewInmate } from '../inmate.model';

export type PartialUpdateInmate = Partial<IInmate> & Pick<IInmate, 'id'>;

type RestOf<T extends IInmate | NewInmate> = Omit<T, 'dateOfBirth' | 'dateOfIncarceration' | 'dateOfExpectedRelease'> & {
  dateOfBirth?: string | null;
  dateOfIncarceration?: string | null;
  dateOfExpectedRelease?: string | null;
};

export type RestInmate = RestOf<IInmate>;

export type NewRestInmate = RestOf<NewInmate>;

export type PartialUpdateRestInmate = RestOf<PartialUpdateInmate>;

export type EntityResponseType = HttpResponse<IInmate>;
export type EntityArrayResponseType = HttpResponse<IInmate[]>;

@Injectable({ providedIn: 'root' })
export class InmateService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/inmates');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(inmate: NewInmate): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(inmate);
    return this.http
      .post<RestInmate>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(inmate: IInmate): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(inmate);
    return this.http
      .put<RestInmate>(`${this.resourceUrl}/${this.getInmateIdentifier(inmate)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(inmate: PartialUpdateInmate): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(inmate);
    return this.http
      .patch<RestInmate>(`${this.resourceUrl}/${this.getInmateIdentifier(inmate)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestInmate>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestInmate[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getInmateIdentifier(inmate: Pick<IInmate, 'id'>): number {
    return inmate.id;
  }

  compareInmate(o1: Pick<IInmate, 'id'> | null, o2: Pick<IInmate, 'id'> | null): boolean {
    return o1 && o2 ? this.getInmateIdentifier(o1) === this.getInmateIdentifier(o2) : o1 === o2;
  }

  addInmateToCollectionIfMissing<Type extends Pick<IInmate, 'id'>>(
    inmateCollection: Type[],
    ...inmatesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const inmates: Type[] = inmatesToCheck.filter(isPresent);
    if (inmates.length > 0) {
      const inmateCollectionIdentifiers = inmateCollection.map(inmateItem => this.getInmateIdentifier(inmateItem)!);
      const inmatesToAdd = inmates.filter(inmateItem => {
        const inmateIdentifier = this.getInmateIdentifier(inmateItem);
        if (inmateCollectionIdentifiers.includes(inmateIdentifier)) {
          return false;
        }
        inmateCollectionIdentifiers.push(inmateIdentifier);
        return true;
      });
      return [...inmatesToAdd, ...inmateCollection];
    }
    return inmateCollection;
  }

  protected convertDateFromClient<T extends IInmate | NewInmate | PartialUpdateInmate>(inmate: T): RestOf<T> {
    return {
      ...inmate,
      dateOfBirth: inmate.dateOfBirth?.format(DATE_FORMAT) ?? null,
      dateOfIncarceration: inmate.dateOfIncarceration?.format(DATE_FORMAT) ?? null,
      dateOfExpectedRelease: inmate.dateOfExpectedRelease?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restInmate: RestInmate): IInmate {
    return {
      ...restInmate,
      dateOfBirth: restInmate.dateOfBirth ? dayjs(restInmate.dateOfBirth) : undefined,
      dateOfIncarceration: restInmate.dateOfIncarceration ? dayjs(restInmate.dateOfIncarceration) : undefined,
      dateOfExpectedRelease: restInmate.dateOfExpectedRelease ? dayjs(restInmate.dateOfExpectedRelease) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestInmate>): HttpResponse<IInmate> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestInmate[]>): HttpResponse<IInmate[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
