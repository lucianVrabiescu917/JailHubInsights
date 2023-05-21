import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { InmateComponent } from '../list/inmate.component';
import { InmateDetailComponent } from '../detail/inmate-detail.component';
import { InmateUpdateComponent } from '../update/inmate-update.component';
import { InmateRoutingResolveService } from './inmate-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const inmateRoute: Routes = [
  {
    path: '',
    component: InmateComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: InmateDetailComponent,
    resolve: {
      inmate: InmateRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: InmateUpdateComponent,
    resolve: {
      inmate: InmateRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: InmateUpdateComponent,
    resolve: {
      inmate: InmateRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(inmateRoute)],
  exports: [RouterModule],
})
export class InmateRoutingModule {}
