export interface IPrison {
  id: number;
  name?: string | null;
  location?: string | null;
}

export type NewPrison = Omit<IPrison, 'id'> & { id: null };
