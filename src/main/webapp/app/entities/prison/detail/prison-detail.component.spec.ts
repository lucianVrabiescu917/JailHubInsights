import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PrisonDetailComponent } from './prison-detail.component';

describe('Prison Management Detail Component', () => {
  let comp: PrisonDetailComponent;
  let fixture: ComponentFixture<PrisonDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PrisonDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ prison: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PrisonDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PrisonDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load prison on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.prison).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
