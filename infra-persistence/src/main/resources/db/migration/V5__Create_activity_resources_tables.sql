CREATE TABLE resources (
	id serial NOT NULL,
	name varchar NOT NULL,
	description varchar(2500),
	CONSTRAINT pk_resources PRIMARY KEY (id)
);

CREATE TABLE activity_resources (
	activity_id integer NOT NULL,
	resource_id integer NOT NULL,
	quantity integer NOT NULL,
	quantity_per_participant boolean NOT NULL default false,
	CONSTRAINT pk_activity_resources PRIMARY KEY (activity_id, resource_id),
	CONSTRAINT fk_activity_resources_activities FOREIGN KEY (activity_id) REFERENCES activities(id),
	CONSTRAINT fk_activity_resources_resources FOREIGN KEY (resource_id) REFERENCES resources(id)
);