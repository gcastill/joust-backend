select
*
from external_profile_source eps
join user_profile up  on eps.user_profile_id = up.user_profile_id
where source = :SOURCE
and reference_id = :REFERENCE_ID
