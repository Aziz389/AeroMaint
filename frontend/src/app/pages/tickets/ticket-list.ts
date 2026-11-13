import { DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { Router, RouterLink } from '@angular/router';
import { errorMessage } from '../../core/api-error';
import { AuthService } from '../../core/auth.service';
import { EquipmentService } from '../../core/equipment.service';
import {
  Equipment,
  Ticket,
  TICKET_PRIORITIES,
  TICKET_STATUSES,
  TicketFilters,
  TicketPriority,
  TicketStatus,
  User,
} from '../../core/models';
import { TicketService } from '../../core/ticket.service';
import { UserService } from '../../core/user.service';

/**
 * Ticket list with filters (status, priority, equipment, assignee).
 * Each filter change calls the API again; filtering is done by the backend query.
 * Technicians only receive their own tickets (enforced by the backend), so the assignee filter is hidden for them.
 */
@Component({
  selector: 'app-ticket-list',
  imports: [
    DatePipe, RouterLink, ReactiveFormsModule,
    MatTableModule, MatFormFieldModule, MatSelectModule, MatButtonModule, MatIconModule, MatProgressBarModule,
  ],
  templateUrl: './ticket-list.html',
})
export class TicketList {
  protected readonly auth = inject(AuthService);
  private readonly ticketService = inject(TicketService);
  private readonly router = inject(Router);

  readonly statuses = TICKET_STATUSES;
  readonly priorities = TICKET_PRIORITIES;
  readonly columns = ['id', 'title', 'equipment', 'priority', 'status', 'assignee', 'createdAt'];

  readonly tickets = signal<Ticket[]>([]);
  readonly equipment = signal<Equipment[]>([]);
  readonly technicians = signal<User[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly filters = inject(FormBuilder).group({
    status: [null as TicketStatus | null],
    priority: [null as TicketPriority | null],
    equipmentId: [null as number | null],
    assigneeId: [null as number | null],
  });

  constructor() {
    // Drop-down options
    inject(EquipmentService).list().subscribe((list) => this.equipment.set(list));
    if (this.auth.hasRole('SUPERVISOR', 'ADMIN')) {
      inject(UserService).technicians().subscribe((list) => this.technicians.set(list));
    }

    // Reload whenever a filter changes (takeUntilDestroyed stops listening when we leave the page)
    this.filters.valueChanges.pipe(takeUntilDestroyed()).subscribe(() => this.load());
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);
    this.ticketService.list(this.filters.getRawValue() as TicketFilters).subscribe({
      next: (list) => {
        this.tickets.set(list);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }

  clearFilters(): void {
    this.filters.reset();
  }

  open(ticket: Ticket): void {
    this.router.navigate(['/tickets', ticket.id]);
  }
}
