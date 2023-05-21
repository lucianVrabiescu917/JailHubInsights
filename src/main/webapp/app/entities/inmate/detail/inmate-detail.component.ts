import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IInmate } from '../inmate.model';

@Component({
  selector: 'jhi-inmate-detail',
  templateUrl: './inmate-detail.component.html',
})
export class InmateDetailComponent implements OnInit {
  inmate: IInmate | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ inmate }) => {
      this.inmate = inmate;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
