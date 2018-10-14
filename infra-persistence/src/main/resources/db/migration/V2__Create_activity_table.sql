CREATE TABLE activities (
	id serial NOT NULL,
	title varchar NOT NULL,
	summary varchar NOT NULL,
	published bool NOT NULL DEFAULT false,
	CONSTRAINT pk_activities PRIMARY KEY (id)
)