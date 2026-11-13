import { DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { errorMessage } from '../../core/api-error';
import { AuthService } from '../../core/auth.service';
import { todayIso } from '../../core/dates';
import { EquipmentService } from '../../core/equipment.service';
import { MaintenancePlanService } from '../../core/maintenance-plan.service';
import { Equipment, MaintenancePlan, MaintenancePlanRequest } from '../../core/models';

/**
 * Preventive maintenance plans. SUPERVISOR adds, edits and deletes; ADMIN only reads.
 * The scheduler on the backend turns due plans into tickets automatically.
 */
@Component({
  selector: 'app-maintenance-plans',
  imports: [
    DatePipe, ReactiveFormsModule,
    MatTableModule, MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, MatIconModule,
  ],
  templateUrl: './maintenance-plans.html',
})
export class MaintenancePlans {
  protected readonly auth = inject(AuthService);
  private readonly planService = inject(MaintenancePlanService);
  private readonly snackBar = inject(MatSnackBar);

  readonly plans = signal<MaintenancePlan[]>([]);
  readonly equipment = signal<Equipment[]>([]);
  readonly error = signal<string | null>(null);
  /** null = form hidden, 0 = adding a new plan, otherwise the id of the plan being edited. */
  readonly editingId = signal<number | null>(null);

  readonly canManage = computed(() => this.auth.hasRole('SUPERVISOR'));
  readonly columns = computed(() => {
    const cols = ['equipment', 'description', 'interval', 'nextDue'];
    return this.canManage() ? [...cols, 'actions'] : cols;
  });
  readonly today = todayIso();

  readonly form = inject(FormBuilder).group({
    equipmentId: [null as number | null, Validators.required],
    intervalDays: [30 as number | null, [Validators.required, Validators.min(1), Validators.max(3650)]],
    nextDue: ['', Validators.required],
    description: ['', [Validators.required, Validators.maxLength(255)]],
  });

  constructor() {
    this.load();
    inject(EquipmentService).list().subscribe((list) => this.equipment.set(list));
  }

  load(): void {
    this.planService.list().subscribe({
      next: (list) => this.plans.set(list),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }

  /** Past or today = the scheduler will create a ticket on its next run. */
  isDue(plan: MaintenancePlan): boolean {
    return plan.nextDue <= this.today;
  }

  startAdd(): void {
    this.form.reset({ equipmentId: null, intervalDays: 30, nextDue: this.today, description: '' });
    this.editingId.set(0);
  }

  startEdit(plan: MaintenancePlan): void {
    this.form.reset({
      equipmentId: plan.equipmentId,
      intervalDays: plan.intervalDays,
      nextDue: plan.nextDue,
      description: plan.description,
    });
    this.editingId.set(plan.id);
  }

  cancel(): void {
    this.editingId.set(null);
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    const request: MaintenancePlanRequest = {
      equipmentId: value.equipmentId!,
      intervalDays: value.intervalDays!,
      nextDue: value.nextDue!,
      description: value.description!.trim(),
    };
    const id = this.editingId();
    const call = id ? this.planService.update(id, request) : this.planService.create(request);
    call.subscribe({
      next: () => {
        this.snackBar.open(id ? 'Plan updated' : 'Plan added', 'OK', { duration: 3000 });
        this.editingId.set(null);
        this.load();
      },
      error: (err) => this.snackBar.open(errorMessage(err), 'OK', { duration: 5000 }),
    });
  }

  delete(plan: MaintenancePlan): void {
    if (!confirm(`Delete the plan "${plan.description}"?`)) return;
    this.planService.delete(plan.id).subscribe({
      next: () => {
        this.snackBar.open('Plan deleted', 'OK', { duration: 3000 });
        this.load();
      },
      error: (err) => this.snackBar.open(errorMessage(err), 'OK', { duration: 5000 }),
    });
  }
}
