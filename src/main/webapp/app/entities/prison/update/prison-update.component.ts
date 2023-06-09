import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { PrisonFormService, PrisonFormGroup } from './prison-form.service';
import { IPrison } from '../prison.model';
import { PrisonService } from '../service/prison.service';

@Component({
  selector: 'jhi-prison-update',
  templateUrl: './prison-update.component.html',
})
export class PrisonUpdateComponent implements OnInit {
  isSaving = false;
  prison: IPrison | null = null;
  selectedImage: string | ArrayBuffer | null = null;

  editForm: PrisonFormGroup = this.prisonFormService.createPrisonFormGroup();

  constructor(
    protected prisonService: PrisonService,
    protected prisonFormService: PrisonFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ prison }) => {
      this.prison = prison;
      if (this.prison != null && this.prison.image != undefined) {
        this.selectedImage = this.prison.image;
      }
      if (prison) {
        this.updateForm(prison);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const prison = this.prisonFormService.getPrison(this.editForm);
    prison.image = this.selectedImage as string;
    if (prison.id !== null) {
      this.subscribeToSaveResponse(this.prisonService.update(prison));
    } else {
      this.subscribeToSaveResponse(this.prisonService.create(prison));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPrison>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(prison: IPrison): void {
    this.prison = prison;
    this.prisonFormService.resetForm(this.editForm, prison);
  }

  onImageSelected(image: string) {
    this.selectedImage = image;
  }
}
