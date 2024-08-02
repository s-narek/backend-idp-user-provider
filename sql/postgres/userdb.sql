CREATE TABLE postgres.public.agreement_type (
                                                id SERIAL PRIMARY KEY,
                                                code VARCHAR(255) NOT NULL,
                                                description VARCHAR(255) NOT NULL
);

CREATE TABLE postgres.public.user (
                                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                      external_id UUID,
                                      first_name VARCHAR(255),
                                      middle_name VARCHAR(255),
                                      last_name VARCHAR(255),
                                      personnel_number VARCHAR(255)
);

CREATE TABLE postgres.public.phone (
                                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                       user_id UUID REFERENCES "user"(id) ON DELETE CASCADE,
                                       phone VARCHAR(50),
                                       type VARCHAR(20) CHECK (type IN ('MAIN', 'WORK', 'OTHER'))
);

CREATE TABLE postgres.public.email (
                                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                       user_id UUID REFERENCES "user"(id) ON DELETE CASCADE,
                                       email VARCHAR(255),
                                       type VARCHAR(20) CHECK (type IN ('PRIVATE', 'WORK', 'CORRESPONDENCE', 'OTHER'))
);

CREATE TABLE postgres.public.user_agreement (
                                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                user_id UUID REFERENCES "user"(id) ON DELETE CASCADE,
                                                value VARCHAR(20) CHECK (value IN ('SIGNED', 'ABSENT', 'WITHDRAW', 'EXPIRED')),
                                                type_id INTEGER REFERENCES agreement_type(id) ON DELETE CASCADE
);

INSERT INTO postgres.public.agreement_type (code, description) VALUES
                                                                   ('AG001', 'Type 1'),
                                                                   ('AG002', 'Type 2'),
                                                                   ('AG003', 'Type 3');

INSERT INTO postgres.public.user (external_id, first_name, middle_name, last_name, personnel_number) VALUES
                                                                                                         (gen_random_uuid(), 'John', 'A.', 'Doe', '12345'),
                                                                                                         (gen_random_uuid(), 'Jane', 'B.', 'Smith', '12346'),
                                                                                                         (gen_random_uuid(), 'Alice', 'C.', 'Johnson', '12347');

INSERT INTO postgres.public.phone (user_id, phone, type) VALUES
                                                             ((SELECT id FROM postgres.public.user WHERE first_name = 'John' AND last_name = 'Doe'), '555-555-5555', 'MAIN'),
                                                             ((SELECT id FROM postgres.public.user WHERE first_name = 'Jane' AND last_name = 'Smith'), '555-555-5556', 'WORK'),
                                                             ((SELECT id FROM postgres.public.user WHERE first_name = 'Alice' AND last_name = 'Johnson'), '555-555-5557', 'OTHER');

INSERT INTO postgres.public.email (user_id, email, type) VALUES
                                                             ((SELECT id FROM postgres.public.user WHERE first_name = 'John' AND last_name = 'Doe'), 'john.doe@example.com', 'PRIVATE'),
                                                             ((SELECT id FROM postgres.public.user WHERE first_name = 'Jane' AND last_name = 'Smith'), 'jane.smith@example.com', 'WORK'),
                                                             ((SELECT id FROM postgres.public.user WHERE first_name = 'Alice' AND last_name = 'Johnson'), 'alice.johnson@example.com', 'CORRESPONDENCE');

INSERT INTO postgres.public.user_agreement (user_id, value, type_id) VALUES
                                                                         ((SELECT id FROM postgres.public.user WHERE first_name = 'John' AND last_name = 'Doe'), 'SIGNED', 1),
                                                                         ((SELECT id FROM postgres.public.user WHERE first_name = 'Jane' AND last_name = 'Smith'), 'ABSENT', 2),
                                                                         ((SELECT id FROM postgres.public.user WHERE first_name = 'Alice' AND last_name = 'Johnson'), 'WITHDRAW', 3);
