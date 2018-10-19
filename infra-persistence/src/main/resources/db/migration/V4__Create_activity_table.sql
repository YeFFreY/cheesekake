CREATE TABLE activities (
	id serial NOT NULL,
	title varchar NOT NULL,
	summary varchar NOT NULL,
	published bool NOT NULL DEFAULT false,
	author_id integer NOT NULL,
	CONSTRAINT pk_activities PRIMARY KEY (id),
	CONSTRAINT fk_users FOREIGN KEY (author_id) REFERENCES users(id)
);