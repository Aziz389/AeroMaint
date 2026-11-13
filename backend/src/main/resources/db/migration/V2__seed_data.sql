-- Fake demo data only. Names, emails and serials are invented.
-- Passwords: 'NOT_SET' is not a valid hash, so nobody can log in yet.
-- Phase 2 (authentication) sets real demo passwords from an environment variable.

INSERT INTO users (id, username, full_name, email, password_hash, role, active, created_at) VALUES
    (1, 'admin',       'Demo Admin',      'admin@aeromaint.example',       'NOT_SET', 'ADMIN',      TRUE, '2026-01-05 08:00:00'),
    (2, 'sup.ben',     'Sami Ben Salah',  'sami.bensalah@aeromaint.example', 'NOT_SET', 'SUPERVISOR', TRUE, '2026-01-05 08:10:00'),
    (3, 'sup.trabelsi','Leila Trabelsi',  'leila.trabelsi@aeromaint.example','NOT_SET', 'SUPERVISOR', TRUE, '2026-01-05 08:20:00'),
    (4, 'tech.karim',  'Karim Haddad',    'karim.haddad@aeromaint.example',  'NOT_SET', 'TECHNICIAN', TRUE, '2026-01-06 09:00:00'),
    (5, 'tech.nour',   'Nour Jebali',     'nour.jebali@aeromaint.example',   'NOT_SET', 'TECHNICIAN', TRUE, '2026-01-06 09:10:00'),
    (6, 'tech.youssef','Youssef Mansour', 'youssef.mansour@aeromaint.example','NOT_SET','TECHNICIAN', TRUE, '2026-01-06 09:20:00'),
    (7, 'tech.ines',   'Ines Gharbi',     'ines.gharbi@aeromaint.example',   'NOT_SET', 'TECHNICIAN', TRUE, '2026-01-06 09:30:00');

INSERT INTO zones (id, name, description) VALUES
    (1, 'Terminal 1',    'Departures and arrivals hall, gates A1-A12'),
    (2, 'Terminal 2',    'International terminal, gates B1-B10'),
    (3, 'Baggage Hall',  'Baggage sorting and reclaim area'),
    (4, 'Apron',         'Aircraft parking stands and ground support area'),
    (5, 'Runway & Taxiways', 'Airfield lighting and navigation aids'),
    (6, 'Car Park',      'Public multi-storey car park');

INSERT INTO equipment (id, name, type, zone_id, status, installed_at) VALUES
    (1,  'Escalator T1-01',            'ESCALATOR',          1, 'OPERATIONAL',       '2018-03-12'),
    (2,  'Elevator T1-02',             'ELEVATOR',           1, 'OPERATIONAL',       '2018-03-15'),
    (3,  'Security X-ray T1-SC1',      'XRAY_SCANNER',       1, 'UNDER_MAINTENANCE', '2021-06-01'),
    (4,  'Jet Bridge B3',              'JET_BRIDGE',         2, 'OPERATIONAL',       '2019-09-20'),
    (5,  'Jet Bridge B5',              'JET_BRIDGE',         2, 'OUT_OF_SERVICE',    '2019-09-20'),
    (6,  'HVAC Unit T2-North',         'HVAC',               2, 'OPERATIONAL',       '2017-05-04'),
    (7,  'Baggage Conveyor BC-1',      'BAGGAGE_CONVEYOR',   3, 'OPERATIONAL',       '2016-11-30'),
    (8,  'Baggage Conveyor BC-2',      'BAGGAGE_CONVEYOR',   3, 'OPERATIONAL',       '2016-11-30'),
    (9,  'Hold Baggage Scanner HBS-1', 'XRAY_SCANNER',       3, 'OPERATIONAL',       '2022-02-14'),
    (10, 'Ground Power Unit GPU-4',    'GROUND_POWER_UNIT',  4, 'OPERATIONAL',       '2020-07-08'),
    (11, 'Backup Generator GEN-A',     'GENERATOR',          4, 'OPERATIONAL',       '2015-04-22'),
    (12, 'Runway Edge Lights RWY-09',  'AIRFIELD_LIGHTING',  5, 'OPERATIONAL',       '2014-10-01'),
    (13, 'Parking Barrier P-Entry',    'PARKING_BARRIER',    6, 'OPERATIONAL',       '2023-01-17');

INSERT INTO tickets (id, equipment_id, reporter_id, assignee_id, title, description, priority, status, created_at, resolved_at) VALUES
    (1,  5,  2, 4,    'Jet bridge B5 will not extend',        'Hydraulic pump stops after 2 seconds. Aircraft boarded by stairs.', 'CRITICAL', 'IN_PROGRESS', '2026-11-10 06:45:00', NULL),
    (2,  3,  3, 5,    'X-ray belt jams at checkpoint 1',      'Belt stops every 10-15 bags. Queue building up at peak hours.',   'HIGH',     'ASSIGNED',    '2026-11-11 07:20:00', NULL),
    (3,  1,  2, NULL, 'Escalator T1-01 making grinding noise','Loud noise near the top landing, still running.',                 'MEDIUM',   'OPEN',        '2026-11-12 10:05:00', NULL),
    (4,  7,  3, 6,    'Conveyor BC-1 motor overheating',      'Motor temperature alarm triggered twice this week.',              'HIGH',     'RESOLVED',    '2026-11-03 14:30:00', '2026-11-04 11:15:00'),
    (5,  7,  2, 6,    'Conveyor BC-1 belt misaligned',        'Bags falling off at the curve section.',                          'MEDIUM',   'CLOSED',      '2026-10-20 09:00:00', '2026-10-20 16:40:00'),
    (6,  6,  3, 7,    'HVAC T2-North not cooling',            'Gate area B1-B4 temperature above 28 C.',                         'HIGH',     'CLOSED',      '2026-10-15 13:10:00', '2026-10-16 10:00:00'),
    (7,  13, 2, 7,    'Parking barrier stuck open',           'Barrier arm does not lower after a ticket is taken.',             'LOW',      'RESOLVED',    '2026-11-08 08:00:00', '2026-11-08 12:30:00'),
    (8,  12, 3, NULL, 'Three runway edge lights out',         'Lights 14, 15 and 22 on RWY 09 reported off by ATC.',             'CRITICAL', 'OPEN',        '2026-11-13 05:50:00', NULL),
    (9,  4,  2, 4,    'Jet bridge B3 canopy torn',            'Rain gets inside the canopy during boarding.',                    'LOW',      'CLOSED',      '2026-09-28 11:00:00', '2026-10-02 15:00:00'),
    (10, 7,  3, 5,    'Conveyor BC-1 emergency stop faulty',  'E-stop button does not reset.',                                   'CRITICAL', 'CLOSED',      '2026-09-12 07:30:00', '2026-09-12 09:10:00');

INSERT INTO ticket_comments (ticket_id, author_id, content, created_at) VALUES
    (1, 4, 'Pump pressure low. Suspect seal failure, ordering spare part.',   '2026-11-10 08:30:00'),
    (1, 2, 'Please keep the operations centre updated every 4 hours.',         '2026-11-10 09:00:00'),
    (2, 5, 'Will inspect the belt tension during the night shift.',            '2026-11-11 09:00:00'),
    (4, 6, 'Replaced the cooling fan and cleaned the motor vents.',            '2026-11-04 11:10:00'),
    (5, 6, 'Belt realigned and tracking rollers adjusted.',                    '2026-10-20 16:30:00'),
    (6, 7, 'Refrigerant topped up, compressor relay replaced.',                '2026-10-16 09:45:00');

-- Preventive maintenance plans. Some next_due dates are already past so the scheduler (Phase 5) has work to do.
INSERT INTO maintenance_plans (equipment_id, interval_days, next_due, description) VALUES
    (1,  30,  '2026-11-20', 'Monthly escalator inspection: steps, handrails, safety switches'),
    (4,  90,  '2026-12-01', 'Quarterly jet bridge hydraulic and drive system check'),
    (7,  14,  '2026-11-10', 'Bi-weekly conveyor belt tension and roller lubrication'),
    (9,  180, '2027-02-14', 'Semi-annual X-ray radiation safety calibration'),
    (11, 30,  '2026-11-01', 'Monthly generator load test'),
    (12, 7,   '2026-11-14', 'Weekly runway lighting visual inspection');
