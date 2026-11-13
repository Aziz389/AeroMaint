import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuditLogEntry, Dashboard } from './models';

/** Statistics (SUPERVISOR, ADMIN) and the audit log (ADMIN). */
@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly http = inject(HttpClient);

  summary(): Observable<Dashboard> {
    return this.http.get<Dashboard>('/api/dashboard');
  }

  auditLog(): Observable<AuditLogEntry[]> {
    return this.http.get<AuditLogEntry[]>('/api/audit-log');
  }
}
