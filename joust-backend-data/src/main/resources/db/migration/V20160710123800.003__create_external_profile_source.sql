CREATE TABLE external_profile_source (
  source varchar(50) not null,
  reference_id text not  null,  
  user_profile_id varchar(50) not null,
  CONSTRAINT external_profile_source_pkey PRIMARY KEY (user_profile_id, source, reference_id),
  CONSTRAINT external_profile_source_user_profile_fkey FOREIGN KEY (user_profile_id) REFERENCES user_profile(user_profile_id)
)
WITH (
 OIDS=FALSE
);