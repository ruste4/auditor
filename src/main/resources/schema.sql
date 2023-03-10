CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    login VARCHAR(50) NOT NULL,
    firstname VARCHAR(255),
    lastname VARCHAR(255),
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_LOGIN UNIQUE (login)
);

CREATE TABLE IF NOT EXISTS storages (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(4000),
    user_id BIGINT NOT NULL,
    CONSTRAINT pk_storage PRIMARY KEY (id),
    CONSTRAINT FK_STORAGE_ON_USER FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(4000),
    storage_id BIGINT NOT NULL,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT FK_ITEM_ON_STORAGE FOREIGN KEY (storage_id) REFERENCES storages (id)
);