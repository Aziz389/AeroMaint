import { DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { errorMessage } from '../../core/api-error';
import { DashboardService } from '../../core/dashboard.service';
import { AuditLogEntry } from '../../core/models';

/** Read-only list of the 200 latest important actions. ADMIN only. */
@Component({
  selector: 'app-audit-log',
  imports: [DatePipe, MatTableModule],
  templateUrl: './audit-log.html',
})
export class AuditLog {
  readonly entries = signal<AuditLogEntry[]>([]);
  readonly error = signal<string | null>(null);
  readonly columns = ['createdAt', 'user', 'action', 'entity', 'details'];

  constructor() {
    inject(DashboardService).auditLog().subscribe({
      next: (list) => this.entries.set(list),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }
}
