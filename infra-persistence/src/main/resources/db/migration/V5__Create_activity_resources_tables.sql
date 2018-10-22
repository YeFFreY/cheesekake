CREATE TABLE resources (
	id serial NOT NULL,
	name varchar NOT NULL,
	CONSTRAINT pk_resources PRIMARY KEY (id)
);

CREATE TABLE activity_resources (
	activity_id integer NOT NULL,
	resource_id integer NOT NULL,
	CONSTRAINT pk_activity_resources PRIMARY KEY (activity_id, resource_id),
	CONSTRAINT fk_activity_resources_activities FOREIGN KEY (activity_id) REFERENCES activities(id),
	CONSTRAINT fk_activity_resources_resources FOREIGN KEY (resource_id) REFERENCES resources(id)
);