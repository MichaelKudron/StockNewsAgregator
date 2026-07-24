import {
  Component,
  Input,
  AfterViewInit,
  OnDestroy,
  OnChanges,
  SimpleChanges,
  ViewChild,
  ElementRef,
} from '@angular/core';

/**
 * Osadzony wykres TradingView. Przyjmuje symbol (np. "GPW:WIG", "GPW:PKN")
 * i renderuje widget "advanced chart". Ten sam mechanizm co w widoku spółki.
 */
@Component({
  selector: 'app-tv-chart',
  standalone: true,
  template: '<div #host class="tv-host"></div>',
  styles: [':host{display:block;height:100%;width:100%}.tv-host{height:100%;width:100%}'],
})
export class TvChartComponent implements AfterViewInit, OnChanges, OnDestroy {
  @Input() symbol = 'GPW:WIG';
  @ViewChild('host', { static: true }) host!: ElementRef<HTMLDivElement>;

  private script: HTMLScriptElement | null = null;
  private ready = false;

  ngAfterViewInit(): void {
    this.ready = true;
    this.render();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.ready && changes['symbol']) {
      this.render();
    }
  }

  private render(): void {
    const container = this.host?.nativeElement;
    if (!container) return;

    container.innerHTML = '';
    this.script?.remove();

    const config = {
      autosize: true,
      symbol: this.symbol,
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
    this.script = script;
  }

  ngOnDestroy(): void {
    this.script?.remove();
  }
}
