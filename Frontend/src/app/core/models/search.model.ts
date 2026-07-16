export interface CompanySearchParams {
  query: string;
  market?: string;
  page: number;
  size: number;
  sortBy: string;
  sortDirection: 'asc' | 'desc';
}
