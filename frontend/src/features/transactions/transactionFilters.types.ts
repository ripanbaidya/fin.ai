import type { TxnType } from "./transaction.types";

export interface FilterState {
  type: TxnType | "";
  dateFrom: string;
  dateTo: string;
}

export const DEFAULT_FILTERS: FilterState = {
  type: "",
  dateFrom: "",
  dateTo: "",
};
