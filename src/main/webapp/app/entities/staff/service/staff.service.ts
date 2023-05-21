import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IStaff, NewStaff } from '../staff.model';

export type PartialUpdateStaff = Partial<IStaff> & Pick<IStaff, 'id'>;

export type EntityResponseType = HttpResponse<IStaff>;
export type EntityArrayResponseType = HttpResponse<IStaff[]>;

@Injectable({ providedIn: 'root' })
export class StaffService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/staff');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(staff: NewStaff): Observable<EntityResponseType> {
    return this.http.post<IStaff>(this.resourceUrl, staff, { observe: 'response' });
  }

  update(staff: IStaff): Observable<EntityResponseType> {
    return this.http.put<IStaff>(`${this.resourceUrl}/${this.getStaffIdentifier(staff)}`, staff, { observe: 'response' });
  }

  partialUpdate(staff: PartialUpdateStaff): Observable<EntityResponseType> {
    return this.http.patch<IStaff>(`${this.resourceUrl}/${this.getStaffIdentifier(staff)}`, staff, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IStaff>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IStaff[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getStaffIdentifier(staff: Pick<IStaff, 'id'>): number {
    return staff.id;
  }

  compareStaff(o1: Pick<IStaff, 'id'> | null, o2: Pick<IStaff, 'id'> | null): boolean {
    return o1 && o2 ? this.getStaffIdentifier(o1) === this.getStaffIdentifier(o2) : o1 === o2;
  }

  addStaffToCollectionIfMissing<Type extends Pick<IStaff, 'id'>>(
    staffCollection: Type[],
    ...staffToCheck: (Type | null | undefined)[]
  ): Type[] {
    const staff: Type[] = staffToCheck.filter(isPresent);
    if (staff.length > 0) {
      const staffCollectionIdentifiers = staffCollection.map(staffItem => this.getStaffIdentifier(staffItem)!);
      const staffToAdd = staff.filter(staffItem => {
        const staffIdentifier = this.getStaffIdentifier(staffItem);
        if (staffCollectionIdentifiers.includes(staffIdentifier)) {
          return false;
        }
        staffCollectionIdentifiers.push(staffIdentifier);
        return true;
      });
      return [...staffToAdd, ...staffCollection];
    }
    return staffCollection;
  }
}
