CREATE TABLE user_profile (
  id varchar(50) not null,
  reference_id text null,
  source varchar(50) not null,
  email character varying(50) not null,
  given_name text not null,
  family_name text null,
  profile_url text null,
  locale varchar(50) not null,
  CONSTRAINT user_profile_pkey PRIMARY KEY (id)
)
WITH (
 OIDS=FALSE
);