import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Company, CompanyAlias, CompanyChartMapping } from '../models/company.model';
import { environment } from '../../../environments/environment';

const API = environment.apiUrl;

@Injectable({ providedIn: 'root' })
export class AdminService {
  private http = inject(HttpClient);

  // ── Company ──────────────────────────────────────────────────────────────
  updateCompany(company: Company): Observable<Company> {
    return this.http.put<Company>(`${API}/api/v1/company`, company);
  }

  // ── Aliases ──────────────────────────────────────────────────────────────
  createAlias(alias: Partial<CompanyAlias>): Observable<CompanyAlias> {
    return this.http.post<CompanyAlias>(`${API}/api/v1/alias`, alias);
  }

  updateAlias(alias: CompanyAlias): Observable<CompanyAlias> {
    return this.http.put<CompanyAlias>(`${API}/api/v1/alias`, alias);
  }

  deleteAlias(id: string): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/alias/id/${id}`);
  }

  // ── Chart mapping ────────────────────────────────────────────────────────
  updateChartMapping(mapping: CompanyChartMapping): Observable<CompanyChartMapping> {
    return this.http.put<CompanyChartMapping>(`${API}/api/v1/company-chart-mapping`, mapping);
  }

  // ── Import ───────────────────────────────────────────────────────────────
  /** Import katalogu spółek z pliku .xlsx (arkusz "companies"). */
  importCompanies(file: File): Observable<void> {
    const form = new FormData();
    form.append('file', file);
    // Content-Type ustawia przeglądarka (multipart boundary) — nie ustawiamy ręcznie.
    return this.http.post<void>(`${API}/api/v1/import/companies`, form);
  }
}
