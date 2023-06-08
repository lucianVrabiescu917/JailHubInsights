import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPrison } from '../prison.model';
import { AccountService } from '../../../core/auth/account.service';
import { takeUntil } from 'rxjs/operators';
import { Account } from '../../../core/auth/account.model';
import { Subject } from 'rxjs';

@Component({
  selector: 'jhi-prison-detail',
  templateUrl: './prison-detail.component.html',
})
export class PrisonDetailComponent implements OnInit {
  prison: IPrison | null = null;
  account: Account | null = null;
  isAdmin: boolean | undefined = false;

  private readonly destroy$ = new Subject<void>();

  constructor(protected activatedRoute: ActivatedRoute, private accountService: AccountService) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ prison }) => {
      this.prison = prison;
    });
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));
    this.isAdmin = this.account?.authorities.includes('ROLE_ADMIN');
  }

  previousState(): void {
    window.history.back();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
