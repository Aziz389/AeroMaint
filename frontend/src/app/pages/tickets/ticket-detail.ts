import { DatePipe } from '@angular/common';
import { Component, computed, inject, input, OnInit, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router, RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
import { errorMessage } from '../../core/api-error';
import { AuthService } from '../../core/auth.service';
import { Ticket, TicketComment, TicketStatus, User } from '../../core/models';
import { TicketService } from '../../core/ticket.service';
import { UserService } from '../../core/user.service';

/** The next step of the status flow, and the label of the button that moves the ticket to it. */
const NEXT_STEP: Partial<Record<TicketStatus, { status: TicketStatus; label: string; icon: string }>> = {
  ASSIGNED: { status: 'IN_PROGRESS', label: 'Start work', icon: 'play_arrow' },
  IN_PROGRESS: { status: 'RESOLVED', label: 'Mark resolved', icon: 'check' },
  RESOLVED: { status: 'CLOSED', label: 'Close ticket', icon: 'task_alt' },
};

/**
 * One ticket: details, workflow buttons and comments.
 * Buttons only appear when the current role may use them. The backend checks the same rules again,
 * so hiding a button is for usability, not for security.
 */
@Component({
  selector: 'app-ticket-detail',
  imports: [
    DatePipe, RouterLink, ReactiveFormsModule,
    MatCardModule, MatButtonModule, MatIconModule, MatFormFieldModule, MatInputModule, MatSelectModule,
  ],
  templateUrl: './ticket-detail.html',
})
export class TicketDetail implements OnInit {
  /** Route parameter :id (bound by withComponentInputBinding). */
  readonly id = input.required<string>();

  protected readonly auth = inject(AuthService);
  private readonly ticketService = inject(TicketService);
  private readonly userService = inject(UserService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly router = inject(Router);

  readonly ticket = signal<Ticket | null>(null);
  readonly comments = signal<TicketComment[]>([]);
  readonly technicians = signal<User[]>([]);
  readonly error = signal<string | null>(null);

  readonly assigneeControl = new FormControl<number | null>(null, Validators.required);
  readonly commentControl = new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.maxLength(2000)] });

  readonly isSupervisor = computed(() => this.auth.hasRole('SUPERVISOR'));

  /** Next status button: technicians may start and resolve; only a supervisor closes. */
  readonly nextStep = computed(() => {
    const ticket = this.ticket();
    const step = ticket ? NEXT_STEP[ticket.status] : undefined;
    if (!step) return null;
    if (this.auth.hasRole('SUPERVISOR')) return step;
    if (this.auth.hasRole('TECHNICIAN') && step.status !== 'CLOSED') return step;
    return null;
  });

  readonly canAssign = computed(() => {
    const status = this.ticket()?.status;
    return this.isSupervisor() && (status === 'OPEN' || status === 'ASSIGNED');
  });

  readonly canComment = computed(
    () => this.auth.hasRole('SUPERVISOR', 'TECHNICIAN') && this.ticket()?.status !== 'CLOSED',
  );

  ngOnInit(): void {
    this.load();
    if (this.isSupervisor()) {
      this.userService.technicians().subscribe((list) => this.technicians.set(list.filter((u) => u.active)));
    }
  }

  load(): void {
    const id = Number(this.id());
    this.ticketService.get(id).subscribe({
      next: (ticket) => {
        this.ticket.set(ticket);
        this.assigneeControl.setValue(ticket.assignee?.id ?? null);
      },
      error: (err) => this.error.set(errorMessage(err)),
    });
    this.ticketService.comments(id).subscribe({
      next: (list) => this.comments.set(list),
      error: () => this.comments.set([]),
    });
  }

  advance(): void {
    const step = this.nextStep();
    const ticket = this.ticket();
    if (!step || !ticket) return;
    this.run(this.ticketService.changeStatus(ticket.id, step.status), `Ticket is now ${step.status}`);
  }

  assign(): void {
    const ticket = this.ticket();
    const assigneeId = this.assigneeControl.value;
    if (!ticket || assigneeId === null) return;
    this.run(this.ticketService.assign(ticket.id, assigneeId), 'Ticket assigned');
  }

  addComment(): void {
    const ticket = this.ticket();
    if (!ticket || this.commentControl.invalid) return;
    this.ticketService.addComment(ticket.id, this.commentControl.value.trim()).subscribe({
      next: (comment) => {
        this.comments.update((list) => [...list, comment]);
        this.commentControl.reset();
      },
      error: (err) => this.notify(errorMessage(err)),
    });
  }

  delete(): void {
    const ticket = this.ticket();
    if (!ticket || !confirm(`Delete ticket #${ticket.id}?`)) return;
    this.ticketService.delete(ticket.id).subscribe({
      next: () => {
        this.notify('Ticket deleted');
        this.router.navigate(['/tickets']);
      },
      error: (err) => this.notify(errorMessage(err)),
    });
  }

  /** Calls the API, then refreshes the ticket on success or shows the backend error message. */
  private run(request: Observable<Ticket>, successMessage: string): void {
    request.subscribe({
      next: (ticket) => {
        this.ticket.set(ticket);
        this.notify(successMessage);
      },
      error: (err) => this.notify(errorMessage(err)),
    });
  }

  private notify(message: string): void {
    this.snackBar.open(message, 'OK', { duration: 4000 });
  }
}
