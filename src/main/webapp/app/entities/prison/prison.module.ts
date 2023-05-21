import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PrisonComponent } from './list/prison.component';
import { PrisonDetailComponent } from './detail/prison-detail.component';
import { PrisonUpdateComponent } from './update/prison-update.component';
import { PrisonDeleteDialogComponent } from './delete/prison-delete-dialog.component';
import { PrisonRoutingModule } from './route/prison-routing.module';

@NgModule({
  imports: [SharedModule, PrisonRoutingModule],
  declarations: [PrisonComponent, PrisonDetailComponent, PrisonUpdateComponent, PrisonDeleteDialogComponent],
})
export class PrisonModule {}
