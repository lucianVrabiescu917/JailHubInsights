import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { InmateComponent } from './list/inmate.component';
import { InmateDetailComponent } from './detail/inmate-detail.component';
import { InmateUpdateComponent } from './update/inmate-update.component';
import { InmateDeleteDialogComponent } from './delete/inmate-delete-dialog.component';
import { InmateRoutingModule } from './route/inmate-routing.module';

@NgModule({
  imports: [SharedModule, InmateRoutingModule],
  declarations: [InmateComponent, InmateDetailComponent, InmateUpdateComponent, InmateDeleteDialogComponent],
})
export class InmateModule {}
