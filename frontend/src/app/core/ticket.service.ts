import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Ticket, TicketComment, TicketFilters, TicketRequest, TicketStatus } from './models';

/** Calls the /api/tickets endpoints. The interceptor adds the JWT automatically. */
@Injectable({ providedIn: 'root' })
export class TicketService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/tickets';

  list(filters: TicketFilters): Observable<Ticket[]> {
    // Only send the filters that have a value: /api/tickets?status=OPEN&priority=HIGH
    let params = new HttpParams();
    for (const [key, value] of Object.entries(filters)) {
      if (value !== null && value !== undefined && value !== '') {
        params = params.set(key, String(value));
      }
    }
    return this.http.get<Ticket[]>(this.url, { params });
  }

  get(id: number): Observable<Ticket> {
    return this.http.get<Ticket>(`${this.url}/${id}`);
  }

  create(request: TicketRequest): Observable<Ticket> {
    return this.http.post<Ticket>(this.url, request);
  }

  update(id: number, request: TicketRequest): Observable<Ticket> {
    return this.http.put<Ticket>(`${this.url}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  assign(id: number, assigneeId: number): Observable<Ticket> {
    return this.http.put<Ticket>(`${this.url}/${id}/assign`, { assigneeId });
  }

  changeStatus(id: number, status: TicketStatus): Observable<Ticket> {
    return this.http.patch<Ticket>(`${this.url}/${id}/status`, { status });
  }

  comments(id: number): Observable<TicketComment[]> {
    return this.http.get<TicketComment[]>(`${this.url}/${id}/comments`);
  }

  addComment(id: number, content: string): Observable<TicketComment> {
    return this.http.post<TicketComment>(`${this.url}/${id}/comments`, { content });
  }
}
