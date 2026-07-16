import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { switchMap, catchError, EMPTY } from 'rxjs';
import { CompanyService } from '../../core/services/company.service';
import { AdminService } from '../../core/services/admin.service';
import { Company, CompanyAlias, CompanyChartMapping, CompanyView } from '../../core/models/company.model';

interface AliasRow extends CompanyAlias {
  editing: boolean;
  draft: { alias: string; priority: number };
}

@Component({
  selector: 'app-company-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './company-admin.component.html',
  styleUrl: './company-admin.component.scss',
})
export class CompanyAdminComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private companyService = inject(CompanyService);
  private adminService = inject(AdminService);

  loading = signal(true);
  error = signal<string | null>(null);
  saving = signal(false);
  toast = signal<{ msg: string; ok: boolean } | null>(null);

  // ── Company form ──────────────────────────────────────────────────────────
  company: Company | null = null;
  companyDraft: Partial<Company> = {};

  // ── Chart mapping form ────────────────────────────────────────────────────
  mapping: CompanyChartMapping | null = null;
  mappingDraft: Partial<CompanyChartMapping> = {};

  // ── Aliases ───────────────────────────────────────────────────────────────
  aliases: AliasRow[] = [];
  newAlias = { alias: '', priority: 0 };
  addingAlias = false;

  ngOnInit(): void {
    this.route.params
      .pipe(
        switchMap(p => this.companyService.getCompanyView(p['isin']).pipe(
          catchError(() => {
            this.error.set('Nie udało się załadować danych spółki.');
            this.loading.set(false);
            return EMPTY;
          })
        ))
      )
      .subscribe((view: CompanyView) => {
        this.company = view.company;
        this.companyDraft = { ...view.company };
        this.mapping = view.companyChartMapping;
        this.mappingDraft = { ...view.companyChartMapping };
        this.aliases = view.companyAliases
          .slice()
          .sort((a, b) => a.priority - b.priority)
          .map(a => ({ ...a, editing: false, draft: { alias: a.alias, priority: a.priority } }));
        this.loading.set(false);
      });
  }

  back(): void {
    this.router.navigate(['/company', this.company?.isin]);
  }

  // ── Save company ──────────────────────────────────────────────────────────
  saveCompany(): void {
    if (!this.company) return;
    this.saving.set(true);
    this.adminService.updateCompany(this.companyDraft as Company).subscribe({
      next: updated => {
        this.company = updated;
        this.companyDraft = { ...updated };
        this.showToast('Dane spółki zapisane', true);
        this.saving.set(false);
      },
      error: () => { this.showToast('Błąd zapisu danych spółki', false); this.saving.set(false); }
    });
  }

  // ── Save chart mapping ────────────────────────────────────────────────────
  saveMapping(): void {
    if (!this.mapping) return;
    this.saving.set(true);
    this.adminService.updateChartMapping(this.mappingDraft as CompanyChartMapping).subscribe({
      next: updated => {
        this.mapping = updated;
        this.mappingDraft = { ...updated };
        this.showToast('Mapowanie wykresu zapisane', true);
        this.saving.set(false);
      },
      error: () => { this.showToast('Błąd zapisu mapowania', false); this.saving.set(false); }
    });
  }

  // ── Alias actions ─────────────────────────────────────────────────────────
  startEdit(row: AliasRow): void {
    row.draft = { alias: row.alias, priority: row.priority };
    row.editing = true;
  }

  cancelEdit(row: AliasRow): void {
    row.editing = false;
  }

  saveAlias(row: AliasRow): void {
    this.adminService.updateAlias({ ...row, alias: row.draft.alias, priority: row.draft.priority }).subscribe({
      next: updated => {
        row.alias = updated.alias;
        row.priority = updated.priority;
        row.editing = false;
        this.aliases.sort((a, b) => a.priority - b.priority);
        this.showToast('Alias zaktualizowany', true);
      },
      error: () => this.showToast('Błąd zapisu aliasu', false)
    });
  }

  deleteAlias(row: AliasRow): void {
    if (!confirm(`Usunąć alias „${row.alias}"?`)) return;
    this.adminService.deleteAlias(row.id).subscribe({
      next: () => {
        this.aliases = this.aliases.filter(a => a.id !== row.id);
        this.showToast('Alias usunięty', true);
      },
      error: () => this.showToast('Błąd usuwania aliasu', false)
    });
  }

  addAlias(): void {
    if (!this.newAlias.alias.trim() || !this.company) return;
    this.adminService.createAlias({
      companyId: this.company.id as any,
      alias: this.newAlias.alias.trim(),
      priority: this.newAlias.priority,
    }).subscribe({
      next: created => {
        this.aliases.push({ ...created, editing: false, draft: { alias: created.alias, priority: created.priority } });
        this.aliases.sort((a, b) => a.priority - b.priority);
        this.newAlias = { alias: '', priority: 0 };
        this.addingAlias = false;
        this.showToast('Alias dodany', true);
      },
      error: () => this.showToast('Błąd dodawania aliasu', false)
    });
  }

  // ── Toast ─────────────────────────────────────────────────────────────────
  private showToast(msg: string, ok: boolean): void {
    this.toast.set({ msg, ok });
    setTimeout(() => this.toast.set(null), 3000);
  }
}
