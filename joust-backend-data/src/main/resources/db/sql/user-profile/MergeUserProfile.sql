insert into user_profile (
  user_profile_id, 
  email, 
  given_name, 
  family_name , 
  profile_url, 
  locale
) 
values (
  :USER_PROFILE_ID, 
  :EMAIL, 
  :GIVEN_NAME, 
  :FAMILY_NAME, 
  :PROFILE_URL, 
  :LOCALE
)
on conflict (user_profile_id)
do update set
  email = :EMAIL,
  given_name = :GIVEN_NAME,
  family_name = :FAMILY_NAME,
  profile_url = :PROFILE_URL,
  locale = :LOCALE
  
