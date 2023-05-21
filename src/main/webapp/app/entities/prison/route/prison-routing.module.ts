import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PrisonComponent } from '../list/prison.component';
import { PrisonDetailComponent } from '../detail/prison-detail.component';
import { PrisonUpdateComponent } from '../update/prison-update.component';
import { PrisonRoutingResolveService } from './prison-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const prisonRoute: Routes = [
  {
    path: '',
    component: PrisonComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PrisonDetailComponent,
    resolve: {
      prison: PrisonRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PrisonUpdateComponent,
    resolve: {
      prison: PrisonRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PrisonUpdateComponent,
    resolve: {
      prison: PrisonRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(prisonRoute)],
  exports: [RouterModule],
})
export class PrisonRoutingModule {}
