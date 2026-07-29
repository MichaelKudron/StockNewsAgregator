import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../core/services/admin.service';

type Status = 'idle' | 'uploading' | 'success' | 'error';

@Component({
  selector: 'app-import',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './import.component.html',
  styleUrl: './import.component.scss',
})
export class ImportComponent {
  private adminService = inject(AdminService);

  file = signal<File | null>(null);
  status = signal<Status>('idle');
  message = signal('');

  onFile(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.file.set(input.files?.[0] ?? null);
    this.status.set('idle');
    this.message.set('');
  }

  upload(): void {
    const f = this.file();
    if (!f || this.status() === 'uploading') return;

    this.status.set('uploading');
    this.message.set('');
    this.adminService.importCompanies(f).subscribe({
      next: () => {
        this.status.set('success');
        this.message.set(`Zaimportowano spółki z pliku „${f.name}".`);
      },
      error: () => {
        this.status.set('error');
        this.message.set('Import nie powiódł się. Upewnij się, że to plik .xlsx z arkuszem „companies".');
      },
    });
  }
}
