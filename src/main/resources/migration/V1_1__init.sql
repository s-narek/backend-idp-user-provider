CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE postgres.r."user" (
                            id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
                            external_id uuid UNIQUE,
                            first_name varchar(64),
                            middle_name varchar(64),
                            last_name varchar(64),
                            personnel_number varchar(65)
);

CREATE TABLE postgres.r.phone (
                       id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
                       user_id uuid NOT NULL,
                       phone varchar(64),
                       type varchar(64),
                       CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES "user"(id)
);

CREATE INDEX phones_user_idx ON postgres.r.phone (user_id);
CREATE INDEX phones_phone_idx ON postgres.r.phone (phone);

CREATE TABLE postgres.r.email (
                        id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
                        user_id uuid NOT NULL,
                        email varchar(64),
                        type varchar(64),
                        CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES "user"(id)
);

CREATE INDEX emails_user_idx ON postgres.r.email (user_id);
CREATE INDEX emails_email_idx ON postgres.r.email (email);

CREATE TABLE postgres.r.agreement_type (
                                id SERIAL PRIMARY KEY,
                                code varchar(32) UNIQUE,
                                description varchar(255)
);

CREATE TABLE postgres.r.user_agreement (
                        id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
                        user_id uuid NOT NULL,
                        value varchar(64),
                        type_id integer NOT NULL,
                        CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES "user"(id),
                        CONSTRAINT fk_type FOREIGN KEY(type_id) REFERENCES agreement_type(id)
);

INSERT INTO agreement_type (code, description) VALUES ('PERSONAL_DATA', 'Согласие на обработку и передачу персональных данных.');
INSERT INTO agreement_type (code, description) VALUES ('BONUS_ACCOUNT', 'Согласие на обработку и передачу данных о бонусном счёте.');
INSERT INTO agreement_type (code, description) VALUES ('IDENTITY', 'Согласие на создание и использование PSB ID.');

CREATE INDEX user_agreements_user_idx ON user_agreement (user_id);
