import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPrison } from '../prison.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../prison.test-samples';

import { PrisonService } from './prison.service';

const requireRestSample: IPrison = {
  ...sampleWithRequiredData,
};

describe('Prison Service', () => {
  let service: PrisonService;
  let httpMock: HttpTestingController;
  let expectedResult: IPrison | IPrison[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PrisonService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Prison', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const prison = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(prison).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Prison', () => {
      const prison = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(prison).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Prison', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Prison', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Prison', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPrisonToCollectionIfMissing', () => {
      it('should add a Prison to an empty array', () => {
        const prison: IPrison = sampleWithRequiredData;
        expectedResult = service.addPrisonToCollectionIfMissing([], prison);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(prison);
      });

      it('should not add a Prison to an array that contains it', () => {
        const prison: IPrison = sampleWithRequiredData;
        const prisonCollection: IPrison[] = [
          {
            ...prison,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPrisonToCollectionIfMissing(prisonCollection, prison);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Prison to an array that doesn't contain it", () => {
        const prison: IPrison = sampleWithRequiredData;
        const prisonCollection: IPrison[] = [sampleWithPartialData];
        expectedResult = service.addPrisonToCollectionIfMissing(prisonCollection, prison);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(prison);
      });

      it('should add only unique Prison to an array', () => {
        const prisonArray: IPrison[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const prisonCollection: IPrison[] = [sampleWithRequiredData];
        expectedResult = service.addPrisonToCollectionIfMissing(prisonCollection, ...prisonArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const prison: IPrison = sampleWithRequiredData;
        const prison2: IPrison = sampleWithPartialData;
        expectedResult = service.addPrisonToCollectionIfMissing([], prison, prison2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(prison);
        expect(expectedResult).toContain(prison2);
      });

      it('should accept null and undefined values', () => {
        const prison: IPrison = sampleWithRequiredData;
        expectedResult = service.addPrisonToCollectionIfMissing([], null, prison, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(prison);
      });

      it('should return initial array if no Prison is added', () => {
        const prisonCollection: IPrison[] = [sampleWithRequiredData];
        expectedResult = service.addPrisonToCollectionIfMissing(prisonCollection, undefined, null);
        expect(expectedResult).toEqual(prisonCollection);
      });
    });

    describe('comparePrison', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePrison(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePrison(entity1, entity2);
        const compareResult2 = service.comparePrison(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePrison(entity1, entity2);
        const compareResult2 = service.comparePrison(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePrison(entity1, entity2);
        const compareResult2 = service.comparePrison(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
