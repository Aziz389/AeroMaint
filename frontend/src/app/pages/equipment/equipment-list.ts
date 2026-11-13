import { DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { Router, RouterLink } from '@angular/router';
import { errorMessage } from '../../core/api-error';
import { AuthService } from '../../core/auth.service';
import { EquipmentService } from '../../core/equipment.service';
import { Equipment } from '../../core/models';

/**
 * Equipment register. Everyone can see it.
 * SUPERVISOR and ADMIN can open an item to see its history; ADMIN can add and edit equipment.
 */
@Component({
  selector: 'app-equipment-list',
  imports: [DatePipe, RouterLink, MatTableModule, MatButtonModule, MatIconModule, MatProgressBarModule],
  templateUrl: './equipment-list.html',
})
export class EquipmentList {
  protected readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly equipment = signal<Equipment[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  readonly canOpenHistory = computed(() => this.auth.hasRole('SUPERVISOR', 'ADMIN'));
  readonly isAdmin = computed(() => this.auth.hasRole('ADMIN'));
  readonly columns = computed(() => {
    const cols = ['name', 'type', 'zone', 'status', 'installedAt'];
    return this.isAdmin() ? [...cols, 'edit'] : cols;
  });

  constructor() {
    inject(EquipmentService).list().subscribe({
      next: (list) => {
        this.equipment.set(list);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }

  open(item: Equipment): void {
    if (this.canOpenHistory()) {
      this.router.navigate(['/equipment', item.id]);
    }
  }
}
