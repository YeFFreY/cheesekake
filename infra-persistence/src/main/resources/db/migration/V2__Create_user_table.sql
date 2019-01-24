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

INSERT INTO users(id) VALUES(DEFAULT);
INSERT INTO credentials(username, password_hash, user_id) VALUES('bob', '$2a$10$DI4nvfO.9L63uXE7ZK0Uaua8SXujw3C6Cw1XFKPN4g9NFJILr21mG', 1);