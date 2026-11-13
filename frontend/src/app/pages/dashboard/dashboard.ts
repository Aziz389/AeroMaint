import { Component, effect, ElementRef, inject, OnDestroy, signal, viewChild } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { Chart, registerables } from 'chart.js';
import { errorMessage } from '../../core/api-error';
import { DashboardService } from '../../core/dashboard.service';
import { Dashboard as DashboardData, TICKET_STATUSES } from '../../core/models';

Chart.register(...registerables);

/**
 * Supervisor / admin dashboard.
 * - Two headline numbers as stat tiles (a single number does not need a chart).
 * - Three horizontal bar charts, one colour each: the bars compare counts, the label names the item.
 */
@Component({
  selector: 'app-dashboard',
  imports: [MatCardModule, MatIconModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnDestroy {
  readonly data = signal<DashboardData | null>(null);
  readonly error = signal<string | null>(null);

  // The <canvas> elements only exist once data has arrived (they are inside @if), so these are signals.
  private readonly statusCanvas = viewChild<ElementRef<HTMLCanvasElement>>('statusChart');
  private readonly equipmentCanvas = viewChild<ElementRef<HTMLCanvasElement>>('equipmentChart');
  private readonly workloadCanvas = viewChild<ElementRef<HTMLCanvasElement>>('workloadChart');
  private charts: Chart[] = [];

  constructor() {
    inject(DashboardService).summary().subscribe({
      next: (d) => this.data.set(d),
      error: (err) => this.error.set(errorMessage(err)),
    });

    // Runs again when data or the canvases change: draws the charts once everything is on the page.
    effect(() => {
      const d = this.data();
      const status = this.statusCanvas();
      const equipment = this.equipmentCanvas();
      const workload = this.workloadCanvas();
      if (!d || !status || !equipment || !workload) return;

      this.destroyCharts();

      // Statuses in workflow order, including those with 0 tickets.
      const byStatus = new Map(d.ticketsByStatus.map((s) => [s.status, s.count]));
      this.charts.push(
        this.barChart(status.nativeElement, TICKET_STATUSES.map((s) => s.replace('_', ' ')),
          TICKET_STATUSES.map((s) => byStatus.get(s) ?? 0), 'Tickets'),
        this.barChart(equipment.nativeElement, d.mostFailingEquipment.map((e) => e.equipmentName),
          d.mostFailingEquipment.map((e) => e.faultCount), 'Fault tickets'),
        this.barChart(workload.nativeElement, d.technicianWorkload.map((t) => t.fullName),
          d.technicianWorkload.map((t) => t.activeTickets), 'Active tickets'),
      );
    });
  }

  ngOnDestroy(): void {
    this.destroyCharts();
  }

  /** One horizontal bar chart with the app's primary colour, whole-number axis and hover tooltips. */
  private barChart(canvas: HTMLCanvasElement, labels: string[], values: number[], label: string): Chart {
    const color = this.themeColor('--mat-sys-primary');
    const ink = this.themeColor('--mat-sys-on-surface-variant');
    const grid = this.themeColor('--mat-sys-outline-variant');

    return new Chart(canvas, {
      type: 'bar',
      data: {
        labels: labels.map((text) => this.wrap(text)),
        datasets: [{ label, data: values, backgroundColor: color, borderRadius: 4, maxBarThickness: 18 }],
      },
      options: {
        indexAxis: 'y',
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          x: { beginAtZero: true, ticks: { precision: 0, color: ink }, grid: { color: grid } },
          y: { ticks: { color: ink }, grid: { display: false } },
        },
      },
    });
  }

  /**
   * Material theme colours are CSS variables written as light-dark(...), which a <canvas> cannot read.
   * Applying the variable to a hidden element lets the browser resolve it to a plain rgb() value.
   */
  private themeColor(variable: string): string {
    const probe = document.createElement('span');
    probe.style.color = `var(${variable})`;
    probe.style.display = 'none';
    document.body.appendChild(probe);
    const resolved = getComputedStyle(probe).color;
    probe.remove();
    return resolved;
  }

  /** Splits a long axis label into two lines (Chart.js draws an array of strings as multiple lines). */
  private wrap(text: string, maxLength = 18): string | string[] {
    if (text.length <= maxLength) return text;
    const words = text.split(' ');
    const lines: string[] = [''];
    for (const word of words) {
      const current = lines[lines.length - 1];
      if (current && (current + ' ' + word).length > maxLength && lines.length < 2) {
        lines.push(word);
      } else {
        lines[lines.length - 1] = current ? `${current} ${word}` : word;
      }
    }
    return lines;
  }

  private destroyCharts(): void {
    this.charts.forEach((chart) => chart.destroy());
    this.charts = [];
  }
}
