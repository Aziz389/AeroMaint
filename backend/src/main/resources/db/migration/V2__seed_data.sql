-- Demo data for Sfax-Thyna International Airport (SFA / DTTX), Tunisia.
-- FICTIONAL: the zones and runway match a small regional airport like Sfax, but the equipment list,
-- staff, tickets and dates are invented. Names and emails are fake.
-- Passwords: 'NOT_SET' is not a valid hash; DemoPasswordInitializer sets them from DEMO_PASSWORD.

INSERT INTO users (id, username, full_name, email, password_hash, role, active, created_at) VALUES
    (1, 'admin',        'Demo Admin',      'admin@aeromaint.example',           'NOT_SET', 'ADMIN',      TRUE, '2026-01-05 08:00:00'),
    (2, 'sup.ben',      'Sami Ben Salah',  'sami.bensalah@aeromaint.example',   'NOT_SET', 'SUPERVISOR', TRUE, '2026-01-05 08:10:00'),
    (3, 'sup.trabelsi', 'Leila Trabelsi',  'leila.trabelsi@aeromaint.example',  'NOT_SET', 'SUPERVISOR', TRUE, '2026-01-05 08:20:00'),
    (4, 'tech.karim',   'Karim Haddad',    'karim.haddad@aeromaint.example',    'NOT_SET', 'TECHNICIAN', TRUE, '2026-01-06 09:00:00'),
    (5, 'tech.nour',    'Nour Jebali',     'nour.jebali@aeromaint.example',     'NOT_SET', 'TECHNICIAN', TRUE, '2026-01-06 09:10:00'),
    (6, 'tech.youssef', 'Youssef Mansour', 'youssef.mansour@aeromaint.example', 'NOT_SET', 'TECHNICIAN', TRUE, '2026-01-06 09:20:00'),
    (7, 'tech.ines',    'Ines Gharbi',     'ines.gharbi@aeromaint.example',     'NOT_SET', 'TECHNICIAN', TRUE, '2026-01-06 09:30:00');

INSERT INTO zones (id, name, description) VALUES
    (1, 'Terminal - Departures',   'Check-in hall, departure lounge and boarding gates'),
    (2, 'Terminal - Arrivals',     'Arrivals hall, passport control and baggage reclaim'),
    (3, 'Security Checkpoint',     'Passenger and cabin baggage screening'),
    (4, 'Baggage Handling Area',   'Hold baggage sorting and screening'),
    (5, 'Apron',                   'Aircraft stands and ground support equipment'),
    (6, 'Runway 15/33 & Taxiways', 'Runway, taxiways and airfield lighting'),
    (7, 'Technical Block',         'Control tower support systems and power station'),
    (8, 'Landside & Car Park',     'Access road and public car park');

INSERT INTO equipment (id, name, type, zone_id, status, installed_at) VALUES
    (1,  'Check-in Conveyor CI-01',             'BAGGAGE_CONVEYOR',  1, 'OPERATIONAL',       '2016-05-10'),
    (2,  'Departure Lounge HVAC AHU-1',         'HVAC',              1, 'OPERATIONAL',       '2015-06-20'),
    (3,  'Flight Information Display FIDS-DEP', 'DISPLAY_SYSTEM',    1, 'OPERATIONAL',       '2021-03-01'),
    (4,  'Passenger Screening X-ray PSX-1',     'XRAY_SCANNER',      3, 'UNDER_MAINTENANCE', '2021-06-01'),
    (5,  'Walk-through Metal Detector WTMD-1',  'METAL_DETECTOR',    3, 'OPERATIONAL',       '2021-06-01'),
    (6,  'Reclaim Carousel RC-1',               'BAGGAGE_CAROUSEL',  2, 'OPERATIONAL',       '2016-05-10'),
    (7,  'Hold Baggage Sorting Conveyor HBS-1', 'BAGGAGE_CONVEYOR',  4, 'OPERATIONAL',       '2016-05-10'),
    (8,  'Hold Baggage X-ray HBX-1',            'XRAY_SCANNER',      4, 'OPERATIONAL',       '2022-02-14'),
    (9,  'Ground Power Unit GPU-2',             'GROUND_POWER_UNIT', 5, 'OPERATIONAL',       '2020-07-08'),
    (10, 'Passenger Boarding Stairs PS-3',      'BOARDING_STAIRS',   5, 'OUT_OF_SERVICE',    '2019-09-20'),
    (11, 'Runway 15/33 Edge Lighting',          'AIRFIELD_LIGHTING', 6, 'OPERATIONAL',       '2014-10-01'),
    (12, 'PAPI Runway 33',                      'AIRFIELD_LIGHTING', 6, 'OPERATIONAL',       '2014-10-01'),
    (13, 'Standby Generator GEN-1',             'GENERATOR',         7, 'OPERATIONAL',       '2015-04-22'),
    (14, 'Car Park Entry Barrier P1',           'PARKING_BARRIER',   8, 'OPERATIONAL',       '2023-01-17');

INSERT INTO tickets (id, equipment_id, reporter_id, assignee_id, title, description, priority, status, created_at, resolved_at) VALUES
    (1,  10, 2, 4,    'Boarding stairs PS-3 will not lift',      'Hydraulic lift stops halfway. Stand served with spare stairs.',      'CRITICAL', 'IN_PROGRESS', '2026-11-10 06:45:00', NULL),
    (2,  4,  3, 5,    'Screening X-ray belt jams',               'Belt stops every 10-15 trays. Queue building up at peak hours.',     'HIGH',     'ASSIGNED',    '2026-11-11 07:20:00', NULL),
    (3,  6,  2, NULL, 'Reclaim carousel RC-1 grinding noise',    'Loud noise near the drive unit, still running.',                     'MEDIUM',   'OPEN',        '2026-11-12 10:05:00', NULL),
    (4,  7,  3, 6,    'Sorting conveyor HBS-1 motor overheating','Motor temperature alarm triggered twice this week.',                 'HIGH',     'RESOLVED',    '2026-11-03 14:30:00', '2026-11-04 11:15:00'),
    (5,  7,  2, 6,    'Sorting conveyor HBS-1 belt misaligned',  'Bags falling off at the curve section.',                             'MEDIUM',   'CLOSED',      '2026-10-20 09:00:00', '2026-10-20 16:40:00'),
    (6,  2,  3, 7,    'Departure lounge HVAC not cooling',       'Lounge temperature above 30 C during the afternoon flights.',        'HIGH',     'CLOSED',      '2026-09-15 13:10:00', '2026-09-16 10:00:00'),
    (7,  14, 2, 7,    'Car park barrier stuck open',             'Barrier arm does not lower after a ticket is taken.',                'LOW',      'RESOLVED',    '2026-11-08 08:00:00', '2026-11-08 12:30:00'),
    (8,  11, 3, NULL, 'Three runway 15/33 edge lights out',      'Lights 14, 15 and 22 reported off by the tower during night check.', 'CRITICAL', 'OPEN',        '2026-11-13 05:50:00', NULL),
    (9,  3,  2, 4,    'Departures FIDS screen frozen',           'Screen at gate 2 shows yesterday''s flights.',                       'LOW',      'CLOSED',      '2026-09-28 11:00:00', '2026-09-28 15:00:00'),
    (10, 7,  3, 5,    'Sorting conveyor HBS-1 emergency stop',   'E-stop button does not reset.',                                      'CRITICAL', 'CLOSED',      '2026-09-12 07:30:00', '2026-09-12 09:10:00');

INSERT INTO ticket_comments (ticket_id, author_id, content, created_at) VALUES
    (1, 4, 'Hydraulic pressure low. Suspect seal failure, ordering spare part.', '2026-11-10 08:30:00'),
    (1, 2, 'Please keep the operations centre updated every 4 hours.',           '2026-11-10 09:00:00'),
    (2, 5, 'Will inspect the belt tension after the last departure.',            '2026-11-11 09:00:00'),
    (4, 6, 'Replaced the cooling fan and cleaned the motor vents.',              '2026-11-04 11:10:00'),
    (5, 6, 'Belt realigned and tracking rollers adjusted.',                      '2026-10-20 16:30:00'),
    (6, 7, 'Refrigerant topped up, compressor relay replaced.',                  '2026-09-16 09:45:00');

-- Preventive maintenance plans. Some next_due dates are already past so the scheduler (Phase 5) has work to do.
INSERT INTO maintenance_plans (equipment_id, interval_days, next_due, description) VALUES
    (6,  30,  '2026-11-20', 'Monthly carousel inspection: drive chain, plates, safety switches'),
    (10, 90,  '2026-12-01', 'Quarterly boarding stairs hydraulic and brake check'),
    (7,  14,  '2026-11-10', 'Bi-weekly conveyor belt tension and roller lubrication'),
    (8,  180, '2027-02-14', 'Semi-annual X-ray radiation safety calibration'),
    (13, 30,  '2026-11-01', 'Monthly standby generator load test'),
    (11, 7,   '2026-11-14', 'Weekly runway 15/33 lighting visual inspection');
