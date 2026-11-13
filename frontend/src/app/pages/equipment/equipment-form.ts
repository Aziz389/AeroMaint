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
import { todayIso } from '../../core/dates';
import { EquipmentService } from '../../core/equipment.service';
import { EQUIPMENT_STATUSES, EquipmentRequest, EquipmentStatus, Zone } from '../../core/models';
import { ZoneService } from '../../core/zone.service';

/** Add (/equipment/new) or edit (/equipment/:id/edit) equipment. ADMIN only (roleGuard). */
@Component({
  selector: 'app-equipment-form',
  imports: [
    ReactiveFormsModule, RouterLink,
    MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, MatIconModule,
  ],
  templateUrl: './equipment-form.html',
})
export class EquipmentForm implements OnInit {
  readonly id = input<string>();
  readonly isEdit = computed(() => this.id() !== undefined);

  private readonly equipmentService = inject(EquipmentService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly router = inject(Router);

  readonly statuses = EQUIPMENT_STATUSES;
  readonly zones = signal<Zone[]>([]);
  readonly saving = signal(false);
  readonly error = signal<string | null>(null);
  /** Today as yyyy-MM-dd, the max value of the installation date picker (backend rule: @PastOrPresent). */
  readonly today = todayIso();

  readonly form = inject(FormBuilder).group({
    name: ['', [Validators.required, Validators.maxLength(100)]],
    type: ['', [Validators.required, Validators.maxLength(50)]],
    zoneId: [null as number | null, Validators.required],
    status: ['OPERATIONAL' as EquipmentStatus, Validators.required],
    installedAt: ['', Validators.required],
  });

  constructor() {
    inject(ZoneService).list().subscribe((list) => this.zones.set(list));
  }

  ngOnInit(): void {
    const id = this.id();
    if (id) {
      this.equipmentService.get(Number(id)).subscribe({
        next: (e) => this.form.patchValue(e),
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
    const request: EquipmentRequest = {
      name: value.name!.trim(),
      type: value.type!.trim().toUpperCase(),
      zoneId: value.zoneId!,
      status: value.status!,
      installedAt: value.installedAt!,
    };

    this.saving.set(true);
    const id = this.id();
    const call = id ? this.equipmentService.update(Number(id), request) : this.equipmentService.create(request);
    call.subscribe({
      next: () => {
        this.snackBar.open(id ? 'Equipment updated' : 'Equipment added', 'OK', { duration: 4000 });
        this.router.navigate(['/equipment']);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.saving.set(false);
      },
    });
  }

  delete(): void {
    const id = this.id();
    if (!id || !confirm('Delete this equipment?')) return;
    this.equipmentService.delete(Number(id)).subscribe({
      next: () => {
        this.snackBar.open('Equipment deleted', 'OK', { duration: 4000 });
        this.router.navigate(['/equipment']);
      },
      // e.g. 409 "Equipment has tickets or maintenance plans; set it OUT_OF_SERVICE instead"
      error: (err) => this.error.set(errorMessage(err)),
    });
  }
}
