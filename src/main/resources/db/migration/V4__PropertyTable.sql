
create table public.property_category
(
    category_id bigserial primary key,
    name        varchar(100) not null,
    description text
);

create table public.properties
(
    property_id bigserial primary key,
    name        varchar(100) not null,
    category_id bigint references public.property_category,
    description text,
    room_id     varchar(100),
    tenant_id   bigint references public.users,
    is_active   boolean default true,
    is_deleted  boolean default false,
    created_at  timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_at  timestamp with time zone default CURRENT_TIMESTAMP not null
);

create table public.room_variant
(
    room_variant_id bigserial primary key,
    property_id bigint references public.properties,
    name varchar(100) not null,
    description text,
    max_guest integer not null,
    price numeric(15,2) not null,
    created_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP not null
);

create table public.room_availability
(
    availability_id bigserial primary key,
    room_variant_id bigint references public.room_variant,
    available_date date not null,
    is_available boolean default true not null,
    created_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP not null
);

create table public.peak_rate
(
    peak_rate_id bigserial primary key,
    room_variant_id bigint references public.room_variant,
    start_date date not null,
    end_date date not null,
    additional_price numeric(15,2) not null,
    created_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP not null
);

create table public.order_status
(
    order_status_id serial primary key,
    name varchar(100) not null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null,
    deleted_at timestamp
);

create table public.order
(
    order_id bigserial primary key,
    user_id bigint references public.users,
    total_price numeric(15,2) not null,
    is_paid boolean default false,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null,
    deleted_at timestamp
);

create table public.order_items
(
    order_item_id bigserial primary key,
    order_id bigint references public.order,
    room_variant_id bigint references public.room_variant,
    start_date date not null,
    end_date date not null,
    total_price numeric(15,2) not null,
    guest integer not null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null,
    deleted_at timestamp
);

create table public.review
(
    review_id bigserial primary key,
    order_id bigint references public.order,
    rating integer not null,
    comment text,
    created_at timestamp with time zone default CURRENT_TIMESTAMP not null
);

create table public.discount
(
    discount_id bigserial primary key,
    property_id bigint references public.properties,
    room_variant_id bigint references public.room_variant,
    start_date date not null,
    end_date date not null,
    discount_type varchar(50) not null,
    value numeric(15,2) not null,
    created_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP not null
);
