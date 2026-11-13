import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Zone } from './models';

/** Calls /api/zones (used to fill the zone drop-down in the equipment form). */
@Injectable({ providedIn: 'root' })
export class ZoneService {
  private readonly http = inject(HttpClient);

  list(): Observable<Zone[]> {
    return this.http.get<Zone[]>('/api/zones');
  }
}
