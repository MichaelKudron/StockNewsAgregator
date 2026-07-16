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
}
