import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { InmateDetailComponent } from './inmate-detail.component';

describe('Inmate Management Detail Component', () => {
  let comp: InmateDetailComponent;
  let fixture: ComponentFixture<InmateDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [InmateDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ inmate: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(InmateDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(InmateDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load inmate on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.inmate).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
