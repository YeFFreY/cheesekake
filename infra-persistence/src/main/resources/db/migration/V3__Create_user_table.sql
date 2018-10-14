--CREATE EXTENSION pgcrypto;

CREATE TABLE credentials (
	id serial NOT NULL,
	username varchar(400) NOT NULL,
	password_hash varchar(100) NOT NULL,
	CONSTRAINT pk_credentials PRIMARY KEY (id),
	CONSTRAINT uk_username UNIQUE (username)
)
