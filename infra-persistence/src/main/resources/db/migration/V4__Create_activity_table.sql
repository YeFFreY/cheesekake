CREATE TABLE activities (
	id serial NOT NULL,
	title varchar(250) NOT NULL,
	summary varchar(2500) NOT NULL,
	published bool NOT NULL DEFAULT false,
	author_id integer not null,
	CONSTRAINT pk_activities PRIMARY KEY (id),
	CONSTRAINT fk_activities_author FOREIGN KEY (author_id) REFERENCES users(id)
);