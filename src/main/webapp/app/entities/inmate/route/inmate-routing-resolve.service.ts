import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IInmate } from '../inmate.model';
import { InmateService } from '../service/inmate.service';

@Injectable({ providedIn: 'root' })
export class InmateRoutingResolveService implements Resolve<IInmate | null> {
  constructor(protected service: InmateService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IInmate | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((inmate: HttpResponse<IInmate>) => {
          if (inmate.body) {
            return of(inmate.body);
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
