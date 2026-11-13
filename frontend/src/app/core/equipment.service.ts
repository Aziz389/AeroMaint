import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Equipment, EquipmentHistory, EquipmentRequest } from './models';

/** Calls the /api/equipment endpoints. */
@Injectable({ providedIn: 'root' })
export class EquipmentService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/equipment';

  list(): Observable<Equipment[]> {
    return this.http.get<Equipment[]>(this.url);
  }

  get(id: number): Observable<Equipment> {
    return this.http.get<Equipment>(`${this.url}/${id}`);
  }

  history(id: number): Observable<EquipmentHistory> {
    return this.http.get<EquipmentHistory>(`${this.url}/${id}/history`);
  }

  create(request: EquipmentRequest): Observable<Equipment> {
    return this.http.post<Equipment>(this.url, request);
  }

  update(id: number, request: EquipmentRequest): Observable<Equipment> {
    return this.http.put<Equipment>(`${this.url}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
