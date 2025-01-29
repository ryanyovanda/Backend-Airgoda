CREATE TABLE categories (
    category_id bigserial PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE events (
    events_id bigserial PRIMARY KEY,
    organizer_id bigint NOT NULL,
    category_id bigint NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(100) NOT NULL,
    event_date TIMESTAMP WITH TIME ZONE NOT NULL,
    is_free BOOLEAN DEFAULT FALSE NOT NULL,
    price NUMERIC(15, 2),
    allocated_seats INTEGER NOT NULL,
    available_seats INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_organizer_id FOREIGN KEY (organizer_id) REFERENCES users(user_id),
    CONSTRAINT fk_category_id FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

