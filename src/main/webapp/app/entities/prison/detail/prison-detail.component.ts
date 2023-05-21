import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPrison } from '../prison.model';

@Component({
  selector: 'jhi-prison-detail',
  templateUrl: './prison-detail.component.html',
})
export class PrisonDetailComponent implements OnInit {
  prison: IPrison | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ prison }) => {
      this.prison = prison;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
