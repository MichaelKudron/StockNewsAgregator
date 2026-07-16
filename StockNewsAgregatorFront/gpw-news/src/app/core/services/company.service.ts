import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CompanyView } from '../models/company.model';
import { CompanySearchParams } from '../models/search.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CompanyService {
  private http = inject(HttpClient);

  getCompanyView(isin: string): Observable<CompanyView> {
    return this.http.get<CompanyView>(
      `${environment.apiUrl}/api/v1/company-view/${isin}`
    );
  }

  searchCompanies(params: CompanySearchParams): Observable<CompanyView[]> {
    return this.http.post<CompanyView[]>(
      `${environment.apiUrl}/api/v1/company-view/search`,
      params
    );
  }
}
