import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MaintenancePlan, MaintenancePlanRequest } from './models';

/** Calls /api/maintenance-plans (SUPERVISOR manages, ADMIN reads). */
@Injectable({ providedIn: 'root' })
export class MaintenancePlanService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/maintenance-plans';

  list(): Observable<MaintenancePlan[]> {
    return this.http.get<MaintenancePlan[]>(this.url);
  }

  create(request: MaintenancePlanRequest): Observable<MaintenancePlan> {
    return this.http.post<MaintenancePlan>(this.url, request);
  }

  update(id: number, request: MaintenancePlanRequest): Observable<MaintenancePlan> {
    return this.http.put<MaintenancePlan>(`${this.url}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
