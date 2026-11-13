// TypeScript shapes of the JSON exchanged with the Spring Boot API (they mirror the backend DTOs).

export type Role = 'TECHNICIAN' | 'SUPERVISOR' | 'ADMIN';
export type TicketStatus = 'OPEN' | 'ASSIGNED' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';
export type TicketPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type EquipmentStatus = 'OPERATIONAL' | 'UNDER_MAINTENANCE' | 'OUT_OF_SERVICE';

export const TICKET_STATUSES: TicketStatus[] = ['OPEN', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'];
export const TICKET_PRIORITIES: TicketPriority[] = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];
export const EQUIPMENT_STATUSES: EquipmentStatus[] = ['OPERATIONAL', 'UNDER_MAINTENANCE', 'OUT_OF_SERVICE'];

export interface User {
  id: number;
  username: string;
  fullName: string;
  email: string;
  role: Role;
  active: boolean;
  createdAt: string;
}

export interface UserSummary {
  id: number;
  username: string;
  fullName: string;
}

export interface LoginResponse {
  token: string;
  expiresInMs: number;
  user: User;
}

export interface Zone {
  id: number;
  name: string;
  description: string | null;
}

export interface Equipment {
  id: number;
  name: string;
  type: string;
  zoneId: number;
  zoneName: string;
  status: EquipmentStatus;
  installedAt: string;
}

export interface EquipmentRequest {
  name: string;
  type: string;
  zoneId: number;
  status: EquipmentStatus;
  installedAt: string;
}

export interface Ticket {
  id: number;
  title: string;
  description: string | null;
  priority: TicketPriority;
  status: TicketStatus;
  equipmentId: number;
  equipmentName: string;
  zoneName: string;
  reporter: UserSummary | null;
  assignee: UserSummary | null;
  createdAt: string;
  resolvedAt: string | null;
}

export interface TicketRequest {
  equipmentId: number;
  title: string;
  description: string | null;
  priority: TicketPriority;
}

export interface TicketFilters {
  status?: TicketStatus | null;
  priority?: TicketPriority | null;
  equipmentId?: number | null;
  assigneeId?: number | null;
}

export interface TicketComment {
  id: number;
  ticketId: number;
  author: UserSummary;
  content: string;
  createdAt: string;
}

export interface MaintenancePlan {
  id: number;
  equipmentId: number;
  equipmentName: string;
  intervalDays: number;
  nextDue: string;
  description: string;
}

export interface MaintenancePlanRequest {
  equipmentId: number;
  intervalDays: number;
  nextDue: string;
  description: string;
}

export interface Dashboard {
  openTickets: number;
  averageResolutionHours: number | null;
  ticketsByStatus: { status: TicketStatus; count: number }[];
  mostFailingEquipment: { equipmentId: number; equipmentName: string; faultCount: number }[];
  technicianWorkload: { technicianId: number; fullName: string; activeTickets: number }[];
}

export interface AuditLogEntry {
  id: number;
  user: UserSummary | null;
  action: string;
  entityType: string;
  entityId: number | null;
  details: string | null;
  createdAt: string;
}

export interface EquipmentHistory {
  equipment: Equipment;
  tickets: Ticket[];
  maintenancePlans: MaintenancePlan[];
}
