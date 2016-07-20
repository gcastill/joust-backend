select
*
from user_profile up 
where 
(:USER_PROFILE_ID IS NULL OR up.user_profile_id = :USER_PROFILE_ID)
and
(:EMAIL IS NULL OR up.email = :EMAIL)
and
(:GIVEN_NAME IS NULL OR up.given_name = :GIVEN_NAME)
and
(:FAMILY_NAME IS NULL OR up.family_name = :FAMILY_NAME)
-- TODO: the below fields are nullable and need to be handled differently
and
(:PROFILE_URL IS NULL OR up.profile_url = :PROFILE_URL)
and
(:LOCALE IS NULL OR up.locale = :LOCALE)
