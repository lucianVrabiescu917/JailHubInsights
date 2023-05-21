import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'prison',
        data: { pageTitle: 'jailHubInsightsApp.prison.home.title' },
        loadChildren: () => import('./prison/prison.module').then(m => m.PrisonModule),
      },
      {
        path: 'inmate',
        data: { pageTitle: 'jailHubInsightsApp.inmate.home.title' },
        loadChildren: () => import('./inmate/inmate.module').then(m => m.InmateModule),
      },
      {
        path: 'staff',
        data: { pageTitle: 'jailHubInsightsApp.staff.home.title' },
        loadChildren: () => import('./staff/staff.module').then(m => m.StaffModule),
      },
      {
        path: 'area',
        data: { pageTitle: 'jailHubInsightsApp.area.home.title' },
        loadChildren: () => import('./area/area.module').then(m => m.AreaModule),
      },
      {
        path: 'activity',
        data: { pageTitle: 'jailHubInsightsApp.activity.home.title' },
        loadChildren: () => import('./activity/activity.module').then(m => m.ActivityModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
