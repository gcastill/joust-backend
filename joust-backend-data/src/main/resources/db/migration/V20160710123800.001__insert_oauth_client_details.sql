INSERT INTO oauth_client_details 
  (client_id, client_secret, authorities, scope, authorized_grant_types) 
VALUES 
  ('nodejs',  'nodejs',  'ROLE_CLIENT', 'read,write,administer' , 'password,refresh_token,client_credentials')
;