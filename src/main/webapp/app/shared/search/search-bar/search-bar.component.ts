import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { EntityArrayResponseType, InmateService } from '../../../entities/inmate/service/inmate.service';
import { IInmate } from '../../../entities/inmate/inmate.model';
import { TOTAL_COUNT_RESPONSE_HEADER } from '../../../config/pagination.constants';
import { combineLatest, Observable, switchMap, tap } from 'rxjs';

@Component({
  selector: 'jhi-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.scss'],
})
export class SearchBarComponent implements OnInit {
  hint: string = '';
  @Input() searchType: string = '';
  @Output() onSearch: EventEmitter<string> = new EventEmitter<string>();

  resultList: any[] = [];
  totalItems: number = 0;

  constructor(private http: HttpClient, protected inmateService: InmateService) {}

  ngOnInit(): void {}

  onInputChange() {
    this.onSearch.emit(this.hint);
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.resultList = dataFromBody;
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  protected fillComponentAttributesFromResponseBody(data: any[] | null): IInmate[] {
    return data ?? [];
  }
}
