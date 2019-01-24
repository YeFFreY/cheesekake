CREATE TABLE activity_categories (
	id serial NOT NULL,
	name varchar NOT NULL,
	description varchar(2500),
	author_id integer not null,
	CONSTRAINT pk_activity_categories PRIMARY KEY (id),
	CONSTRAINT fk_activity_categories_users FOREIGN KEY (author_id) REFERENCES users(id)
);

CREATE TABLE activities (
	id serial NOT NULL,
	category_id integer NOT NULL,
	title varchar(250) NOT NULL,
	summary varchar(2500) NOT NULL,
	published bool NOT NULL DEFAULT false,
	author_id integer not null,
	duration_min integer,
	duration_max integer,
	participants_count_min integer,
	participants_count_max integer,
	participants_age_min integer,
	participants_age_max integer,
	CONSTRAINT pk_activities PRIMARY KEY (id),
	CONSTRAINT fk_activities_users FOREIGN KEY (author_id) REFERENCES users(id),
	CONSTRAINT fk_activities_categories FOREIGN KEY (category_id) REFERENCES activity_categories(id)
);
