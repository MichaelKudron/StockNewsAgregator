# GPW News — Frontend

Angular 18 standalone app do agregacji newsów z polskiej giełdy.

## Start

```bash
npm install
ng serve
```

Otwórz: http://localhost:4200

Domyślnie przekierowuje na `/company/PLPEKAO00016` (Pekao).

## Wymagania

Backend musi działać na `http://localhost:8080`.
Konfiguracja URL w `src/environments/environment.ts`.

## Struktura

```
src/app/
├── core/
│   ├── models/
│   │   └── company.model.ts      # interfejsy Company, CompanyView itd.
│   └── services/
│       └── company.service.ts    # HTTP calls
├── features/
│   └── company-view/             # widok pojedynczej spółki
│       ├── company-view.component.ts
│       ├── company-view.component.html
│       └── company-view.component.scss
├── app.component.ts              # root (tylko <router-outlet>)
├── app.config.ts                 # provideHttpClient, provideRouter
└── app.routes.ts                 # /company/:isin
```

## Dodawanie nowych widoków

```bash
ng generate component features/NAZWA --standalone --style=scss --skip-tests
```

Dodaj trasę w `app.routes.ts` z lazy loadingiem:

```ts
{
  path: 'news',
  loadComponent: () =>
    import('./features/news-list/news-list.component').then(m => m.NewsListComponent),
}
```

## Prod build

```bash
ng build --configuration production
```

Output w `dist/gpw-news/browser/`.
