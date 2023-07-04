export interface IPrison {
  id: number;
  name?: string | null;
  location?: string | null;
  image?: string | null;
  cellRatio?: number | null;
  cellBlockRatio?: number | null;
  diningRatio?: number | null;
  laborRatio?: number | null;
  classRatio?: number | null;
  recreationRatio?: number | null;
}

export type NewPrison = Omit<IPrison, 'id'> & { id: null };
