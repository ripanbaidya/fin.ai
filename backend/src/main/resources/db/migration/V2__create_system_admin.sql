INSERT INTO users (
    id,
    created_at,
    updated_at,
    full_name,
    email,
    password_hash,
    role,
    account_status,
    deletion_requested_at
)
VALUES (
           gen_random_uuid()::VARCHAR,
           now(),
           now(),
           'admin1',
           'admin@walletiq.ai',
           '$2a$10$y8RxtjTTBHEp2GemllrBb.BJBVH.LEj6hDoSJ4J1KzqovZzMbXkGa', -- password12345
           'ADMIN',
           'ACTIVE',
           NULL
       )
ON CONFLICT (email) DO NOTHING;