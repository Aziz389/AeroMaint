import { Component, computed, inject, input, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router, RouterLink } from '@angular/router';
import { errorMessage } from '../../core/api-error';
import { EquipmentService } from '../../core/equipment.service';
import { Equipment, TICKET_PRIORITIES, TicketPriority, TicketRequest } from '../../core/models';
import { TicketService } from '../../core/ticket.service';

/** Create a ticket (/tickets/new) or edit one (/tickets/:id/edit). SUPERVISOR only (roleGuard). */
@Component({
  selector: 'app-ticket-form',
  imports: [
    ReactiveFormsModule, RouterLink,
    MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, MatIconModule,
  ],
  templateUrl: './ticket-form.html',
})
export class TicketForm implements OnInit {
  /** Route parameter :id; undefined on /tickets/new. */
  readonly id = input<string>();
  readonly isEdit = computed(() => this.id() !== undefined);

  private readonly ticketService = inject(TicketService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly router = inject(Router);

  readonly priorities = TICKET_PRIORITIES;
  readonly equipment = signal<Equipment[]>([]);
  readonly saving = signal(false);
  readonly error = signal<string | null>(null);

  // Same rules as the backend TicketRequest (@NotNull, @NotBlank, @Size).
  readonly form = inject(FormBuilder).group({
    equipmentId: [null as number | null, Validators.required],
    title: ['', [Validators.required, Validators.maxLength(150)]],
    description: ['', Validators.maxLength(2000)],
    priority: ['MEDIUM' as TicketPriority, Validators.required],
  });

  constructor() {
    inject(EquipmentService).list().subscribe((list) => this.equipment.set(list));
  }

  ngOnInit(): void {
    const id = this.id();
    if (id) {
      this.ticketService.get(Number(id)).subscribe({
        next: (t) => this.form.patchValue({
          equipmentId: t.equipmentId,
          title: t.title,
          description: t.description ?? '',
          priority: t.priority,
        }),
        error: (err) => this.error.set(errorMessage(err)),
      });
    }
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    const request: TicketRequest = {
      equipmentId: value.equipmentId!,
      title: value.title!.trim(),
      description: value.description?.trim() || null,
      priority: value.priority!,
    };

    this.saving.set(true);
    const id = this.id();
    const call = id ? this.ticketService.update(Number(id), request) : this.ticketService.create(request);

    call.subscribe({
      next: (ticket) => {
        this.snackBar.open(id ? 'Ticket updated' : `Ticket #${ticket.id} created`, 'OK', { duration: 4000 });
        this.router.navigate(['/tickets', ticket.id]);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.saving.set(false);
      },
    });
  }
}
