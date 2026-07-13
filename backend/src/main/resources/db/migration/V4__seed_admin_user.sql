-- Seed data: έτοιμος admin λογαριασμός για επίδειξη/παρουσίαση.
-- Username: admin
-- Password: Demo1234!
-- (Η απάντηση ασφαλείας δεν χρειάζεται για τη σύνδεση — μόνο για reset password.)

INSERT INTO users (
    id, username, full_name, email, password_hash,
    role, is_active, security_question, security_answer_hash,
    created_at
) VALUES (
    'admin-seed-0001',
    'admin',
    'Διαχειριστής Πλατφόρμας',
    'admin@cloudplatform.local',
    '$2b$12$Ivk8ycjyfgdF4p4d4g67ZuNd4LIgxojqUI6CfkFUUqsDrQWK4jhya',
    'ADMIN',
    TRUE,
    'Σε ποιο νησί βρίσκεται το Πανεπιστήμιο Αιγαίου;',
    '$2b$12$YJ.3QndAZ1CTe3GhcgV8f..0ERtDv2GkaaONf3o.IHgNPZhKZHxLC',
    CURRENT_TIMESTAMP
);
