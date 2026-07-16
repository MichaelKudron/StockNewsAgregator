import { Component, OnInit, inject, signal, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged, switchMap, catchError, of, takeUntil } from 'rxjs';
import { CompanyService } from '../../core/services/company.service';
import { CompanyView } from '../../core/models/company.model';
import { CompanySearchParams } from '../../core/models/search.model';

@Component({
  selector: 'app-company-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './company-list.component.html',
  styleUrl: './company-list.component.scss',
})
export class CompanyListComponent implements OnInit, OnDestroy {
  private companyService = inject(CompanyService);
  private router = inject(Router);
  private destroy$ = new Subject<void>();
  private search$ = new Subject<string>();

  companies = signal<CompanyView[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  query = '';
  sortBy = 'ticker';
  sortDirection: 'asc' | 'desc' = 'asc';

  sortOptions = [
    { value: 'ticker', label: 'Ticker — A do Z', direction: 'asc' as const },
    { value: 'ticker', label: 'Ticker — Z do A', direction: 'desc' as const },
    { value: 'name', label: 'Nazwa — A do Z', direction: 'asc' as const },
    { value: 'name', label: 'Nazwa — Z do A', direction: 'desc' as const },
  ];
  selectedSort = 0;

  ngOnInit(): void {
    this.search$
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap(q => {
          this.loading.set(true);
          this.error.set(null);
          return this.companyService.searchCompanies(this.buildParams(q)).pipe(
            catchError(() => {
              this.error.set('Nie udało się załadować listy spółek.');
              this.loading.set(false);
              return of([]);
            })
          );
        }),
        takeUntil(this.destroy$)
      )
      .subscribe(results => {
        this.companies.set(results);
        this.loading.set(false);
      });

    this.search$.next('');
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onQueryChange(): void {
    this.search$.next(this.query);
  }

  onSortChange(index: number): void {
    this.selectedSort = index;
    const opt = this.sortOptions[index];
    this.sortBy = opt.value;
    this.sortDirection = opt.direction;
    this.search$.next(this.query);
  }

  goToCompany(isin: string): void {
    this.router.navigate(['/company', isin]);
  }

  private buildParams(q: string): CompanySearchParams {
    return {
      query: q,
      market: 'Gpw',
      page: 0,
      size: 100,
      sortBy: this.sortBy,
      sortDirection: this.sortDirection,
    };
  }
}
