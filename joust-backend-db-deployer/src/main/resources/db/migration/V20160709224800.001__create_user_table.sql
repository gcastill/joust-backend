CREATE TABLE "user" (
  id bigserial NOT NULL,
  email character varying(20) NOT NULL,
  given_name text NOT NULL,
  family_name text NOT NULL,
  profile_picture text NULL,
  password text NULL,
  facebook_access_token text NULL,
  google_access_token text NULL,
  CONSTRAINT user_pkey PRIMARY KEY (id),
  CONSTRAINT email_uni UNIQUE (email),
  CONSTRAINT facebook_access_token_uni UNIQUE (facebook_access_token),
  CONSTRAINT google_access_token_uni UNIQUE (google_access_token)
)
WITH (
 OIDS=FALSE
);