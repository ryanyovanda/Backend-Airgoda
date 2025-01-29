CREATE TABLE roles (
    role_id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(user_id),
    role_id INTEGER NOT NULL REFERENCES roles(role_id),
    assigned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

-- Seed data for roles table
INSERT INTO
    roles (name, created_at, updated_at)
VALUES
    ('ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (
        'ORGANIZER',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

-- Seed data for users table
INSERT INTO
    users (
    email,
    password,
    pin,
    profile_picture_url,
    is_onboarding_finished,
    created_at,
    updated_at
)
VALUES
    (
        'alice@example.com',
        'hashedpassword1',
        '1234',
        'http://example.com/profiles/alice.jpg',
        true,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'bob@example.com',
        'hashedpassword2',
        '5678',
        'http://example.com/profiles/bob.jpg',
        false,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'charlie@example.com',
        'hashedpassword3',
        '9012',
        'http://example.com/profiles/charlie.jpg',
        true,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

-- Seed data for user_roles table
INSERT INTO
    user_roles (user_id, role_id, assigned_at)
VALUES
    (
        (
            SELECT
                user_id
            FROM
                users
            WHERE
                email = 'alice@example.com'
        ),
        (
            SELECT
                role_id
            FROM
                roles
            WHERE
                name = 'ADMIN'
        ),
        CURRENT_TIMESTAMP
    ),
    (
        (
            SELECT
                user_id
            FROM
                users
            WHERE
                email = 'bob@example.com'
        ),
        (
            SELECT
                role_id
            FROM
                roles
            WHERE
                name = 'USER'
        ),
        CURRENT_TIMESTAMP
    ),
    (
        (
            SELECT
                user_id
            FROM
                users
            WHERE
                email = 'charlie@example.com'
        ),
        (
            SELECT
                role_id
            FROM
                roles
            WHERE
                name = 'ORGANIZER'
        ),
        CURRENT_TIMESTAMP
    );