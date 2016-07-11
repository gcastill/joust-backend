CREATE TABLE user_profile (
  user_profile_id varchar(50) not null,
  email character varying(50) not null,
  given_name text not null,
  family_name text null,
  profile_url text null,
  locale varchar(50) not null,
  CONSTRAINT user_profile_pkey PRIMARY KEY (user_profile_id)
)
WITH (
 OIDS=FALSE
);