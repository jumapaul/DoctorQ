CREATE TABLE user_profile(
    id BIGSERIAL PRIMARY KEY,
    gender VARCHAR(20),
    date_of_birth VARCHAR(20)
    address TEXT
    profile_url VARCHAR(500)
);

CREATE TABLE doctorqusers(
    id BIGSERIAL PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_enabled BOOLEAN DEFAULT FALSE,
    verification_code VARCHAR(6)
    verification_expires_at TIMESTAMP,
    rest_pass_code VARCHAR(6),
    rest_pass_code_expires_at TIMESTAMP,
    role VARCHAR(50) NOT NULL,

    profile_id BIGINT UNIQUE,
    CONSTRAINT fk_user_profile FOREIGN KEY(profile_id)
        REFERENCES user_profile(id)
        ON_DELETE CASCADE
);