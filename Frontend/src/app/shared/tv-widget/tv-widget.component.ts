import {
  Component,
  Input,
  AfterViewInit,
  OnDestroy,
  ViewChild,
  ElementRef,
} from '@angular/core';

/**
 * Uniwersalna owijka na widgety osadzane TradingView (ticker tape, heatmapa,
 * kalendarz ekonomiczny itd.). Wstrzykuje skrypt z konfiguracją JSON do
 * kontenera zgodnie ze standardową strukturą TradingView.
 */
@Component({
  selector: 'app-tv-widget',
  standalone: true,
  template: `
    <div class="tradingview-widget-container" #container [style.height]="height">
      <div class="tradingview-widget-container__widget" [style.height]="height"></div>
    </div>
  `,
  styles: [
    ':host{display:block;width:100%}.tradingview-widget-container{width:100%}',
  ],
})
export class TvWidgetComponent implements AfterViewInit, OnDestroy {
  /** URL skryptu widgetu, np. embed-widget-ticker-tape.js */
  @Input() scriptSrc!: string;
  /** Konfiguracja widgetu (obiekt serializowany do JSON) */
  @Input() config: Record<string, unknown> = {};
  /** Wysokość kontenera, np. "100%" lub "500px" */
  @Input() height = '100%';

  @ViewChild('container', { static: true }) container!: ElementRef<HTMLDivElement>;
  private script: HTMLScriptElement | null = null;

  ngAfterViewInit(): void {
    const script = document.createElement('script');
    script.src = this.scriptSrc;
    script.async = true;
    script.type = 'text/javascript';
    script.innerHTML = JSON.stringify(this.config);
    this.container.nativeElement.appendChild(script);
    this.script = script;
  }

  ngOnDestroy(): void {
    this.script?.remove();
  }
}
