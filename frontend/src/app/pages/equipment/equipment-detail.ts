import { DatePipe } from '@angular/common';
import { Component, computed, inject, input, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { Router, RouterLink } from '@angular/router';
import { errorMessage } from '../../core/api-error';
import { AuthService } from '../../core/auth.service';
import { EquipmentService } from '../../core/equipment.service';
import { EquipmentHistory, Ticket } from '../../core/models';

/** One piece of equipment with its full fault and maintenance history. SUPERVISOR and ADMIN. */
@Component({
  selector: 'app-equipment-detail',
  imports: [DatePipe, RouterLink, MatCardModule, MatTableModule, MatButtonModule, MatIconModule],
  templateUrl: './equipment-detail.html',
})
export class EquipmentDetail implements OnInit {
  readonly id = input.required<string>();

  protected readonly auth = inject(AuthService);
  private readonly equipmentService = inject(EquipmentService);
  private readonly router = inject(Router);

  readonly history = signal<EquipmentHistory | null>(null);
  readonly error = signal<string | null>(null);

  readonly ticketColumns = ['id', 'title', 'priority', 'status', 'assignee', 'createdAt', 'resolvedAt'];
  readonly planColumns = ['description', 'intervalDays', 'nextDue'];

  /** Number of tickets that are not CLOSED yet. */
  readonly openCount = computed(() => this.history()?.tickets.filter((t) => t.status !== 'CLOSED').length ?? 0);

  ngOnInit(): void {
    this.equipmentService.history(Number(this.id())).subscribe({
      next: (h) => this.history.set(h),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }

  openTicket(ticket: Ticket): void {
    this.router.navigate(['/tickets', ticket.id]);
  }
}
