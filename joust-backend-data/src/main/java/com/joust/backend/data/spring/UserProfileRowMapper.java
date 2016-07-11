package com.joust.backend.data.spring;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.joust.backend.core.model.UserProfile;

public class UserProfileRowMapper implements RowMapper<UserProfile> {

	@Override
	public UserProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserProfile result = new UserProfile();
		return result;
	}

}
