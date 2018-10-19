CREATE TABLE users(
	id serial NOT NULL,
	CONSTRAINT pk_users PRIMARY KEY (id)
);
CREATE TABLE credentials (
	id serial NOT NULL,
	username varchar(400) NOT NULL,
	password_hash varchar(100) NOT NULL,
	user_id integer NOT NULL,
	CONSTRAINT pk_credentials PRIMARY KEY (id),
	CONSTRAINT fk_users FOREIGN KEY (user_id) REFERENCES users(id),
	CONSTRAINT uk_username UNIQUE (username)
);
