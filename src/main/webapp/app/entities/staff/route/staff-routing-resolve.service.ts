import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IStaff } from '../staff.model';
import { StaffService } from '../service/staff.service';

@Injectable({ providedIn: 'root' })
export class StaffRoutingResolveService implements Resolve<IStaff | null> {
  constructor(protected service: StaffService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IStaff | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((staff: HttpResponse<IStaff>) => {
          if (staff.body) {
            return of(staff.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
