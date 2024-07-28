CREATE EXTENSION IF NOT EXISTS "uuid-ossp"; -- Для генерации UUID

CREATE TABLE keycloak.public."user" (
                                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                        external_id UUID,
                                        first_name VARCHAR(255),
                                        middle_name VARCHAR(255),
                                        last_name VARCHAR(255),
                                        personnel_number VARCHAR(255)
);

CREATE TABLE keycloak.public."phone" (
                                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                         user_id UUID REFERENCES "user"(id) ON DELETE CASCADE,
                                         phone VARCHAR(50),
                                         type VARCHAR(20) CHECK (type IN ('MAIN', 'WORK', 'OTHER'))
);

CREATE TABLE keycloak.public."email" (
                                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                         user_id UUID REFERENCES "user"(id) ON DELETE CASCADE,
                                         email VARCHAR(255),
                                         type VARCHAR(20) CHECK (type IN ('PRIVATE', 'WORK', 'CORRESPONDENCE', 'OTHER'))
);
