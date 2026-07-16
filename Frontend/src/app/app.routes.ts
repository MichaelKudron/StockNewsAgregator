import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/company-list/company-list.component').then(
        m => m.CompanyListComponent
      ),
    title: 'Spółki GPW',
  },
  {
    path: 'company/:isin',
    loadComponent: () =>
      import('./features/company-view/company-view.component').then(
        m => m.CompanyViewComponent
      ),
    title: 'Spółka — GPW News',
  },
  {
    path: 'company/:isin/admin',
    loadComponent: () =>
      import('./features/company-admin/company-admin.component').then(
        m => m.CompanyAdminComponent
      ),
    title: 'Admin — GPW News',
  },
  {
    path: '**',
    redirectTo: '',
  },
];
