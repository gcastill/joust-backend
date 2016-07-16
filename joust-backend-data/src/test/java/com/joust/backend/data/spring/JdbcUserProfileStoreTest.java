package com.joust.backend.data.spring;



import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.joust.backend.test.meanbean.BeanTesterBuilder;

public class JdbcUserProfileStoreTest {

  @Test
  public void testBean() {

    new BeanTesterBuilder().withFactory(NamedParameterJdbcTemplate.class, () -> {
      return Mockito.mock(NamedParameterJdbcTemplate.class);
    }).withFactory(RowMapper.class, () -> {
      return Mockito.mock(RowMapper.class);
    }).build().testBean(JdbcUserProfileStore.class);

  }
}
