CREATE TABLE skill_categories (
	id serial NOT NULL,
	name varchar NOT NULL,
	description varchar(2500),
	CONSTRAINT pk_skill_categories PRIMARY KEY (id)
);

CREATE TABLE skills (
	id serial NOT NULL,
	name varchar NOT NULL,
	description varchar(2500) NOT NULL,
	category_id integer NOT NULL,
	author_id integer not null,
	CONSTRAINT fk_skills_categories FOREIGN KEY (category_id) REFERENCES skill_categories(id),
	CONSTRAINT fk_skills_users FOREIGN KEY (author_id) REFERENCES users(id),
	CONSTRAINT pk_skills PRIMARY KEY (id)
);

CREATE TABLE activity_skills (
	activity_id integer NOT NULL,
	skill_id integer NOT NULL,
	CONSTRAINT pk_activity_skills PRIMARY KEY (activity_id, skill_id),
	CONSTRAINT fk_activity_skills_activities FOREIGN KEY (activity_id) REFERENCES activities(id),
	CONSTRAINT fk_activity_skills_skills FOREIGN KEY (skill_id) REFERENCES skills(id)
);