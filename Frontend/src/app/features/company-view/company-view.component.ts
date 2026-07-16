import {
  Component,
  OnInit,
  OnDestroy,
  AfterViewInit,
  inject,
  signal,
  ViewChild,
  ElementRef,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { switchMap, catchError, EMPTY } from 'rxjs';
import { CompanyService } from '../../core/services/company.service';
import { CompanyView } from '../../core/models/company.model';

@Component({
  selector: 'app-company-view',
  standalone: true,
  imports: [CommonModule, DatePipe, RouterLink],
  templateUrl: './company-view.component.html',
  styleUrl: './company-view.component.scss',
})
export class CompanyViewComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('tvContainer') tvContainer!: ElementRef<HTMLDivElement>;

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private companyService = inject(CompanyService);

  data = signal<CompanyView | null>(null);
  loading = signal(true);
  error = signal<string | null>(null);

  private tvScript: HTMLScriptElement | null = null;
  private viewReady = false;
  private dataReady = false;

  get company() {
    return this.data()?.company ?? null;
  }

  get chartMapping() {
    return this.data()?.companyChartMapping ?? null;
  }

  /** Top 3 aliasy wg priorytetu, bez duplikatów z nazwą i tickerem */
  get displayAliases(): string[] {
    const c = this.data();
    if (!c) return [];
    const exclude = new Set([c.company.name, c.company.ticker, c.company.shortName]);
    return c.companyAliases
      .sort((a, b) => a.priority - b.priority)
      .map(a => a.alias)
      .filter(a => !exclude.has(a))
      .slice(0, 3);
  }

  get ipoFormatted(): string {
    const d = this.company?.ipoDate;
    if (!d) return '—';
    return new Date(d).toLocaleDateString('pl-PL', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  }

  ngOnInit(): void {
    this.route.params
      .pipe(
        switchMap(params => {
          this.loading.set(true);
          this.error.set(null);
          this.data.set(null);
          return this.companyService.getCompanyView(params['isin']).pipe(
            catchError(() => {
              this.error.set('Nie udało się załadować danych spółki. Sprawdź połączenie z API.');
              this.loading.set(false);
              return EMPTY;
            })
          );
        })
      )
      .subscribe(view => {
        this.data.set(view);
        this.loading.set(false);
        this.dataReady = true;
        this.tryInitChart();
      });
  }

  ngAfterViewInit(): void {
    this.viewReady = true;
    this.tryInitChart();
  }

  private tryInitChart(): void {
    if (!this.viewReady || !this.dataReady) return;
    // give Angular one tick to render the container
    setTimeout(() => this.initTradingView(), 0);
  }

  private initTradingView(): void {
    const container = this.tvContainer?.nativeElement;
    const mapping = this.chartMapping;
    if (!container || !mapping) return;

    container.innerHTML = '';
    this.tvScript?.remove();

    const config = {
      autosize: true,
      symbol: mapping.symbol,
      interval: 'D',
      timezone: 'Europe/Warsaw',
      theme: 'dark',
      style: '1',
      locale: 'pl',
      hide_top_toolbar: false,
      hide_legend: false,
      allow_symbol_change: false,
      save_image: false,
      calendar: false,
      support_host: 'https://www.tradingview.com',
    };

    const script = document.createElement('script');
    script.src =
      'https://s3.tradingview.com/external-embedding/embed-widget-advanced-chart.js';
    script.async = true;
    script.innerHTML = JSON.stringify(config);
    container.appendChild(script);
    this.tvScript = script;
  }

  goToAdmin(): void {
    this.router.navigate(['/company', this.company?.isin, 'admin']);
  }

  ngOnDestroy(): void {
    this.tvScript?.remove();
  }
}
