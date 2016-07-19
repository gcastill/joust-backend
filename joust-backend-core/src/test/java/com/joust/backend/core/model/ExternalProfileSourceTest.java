package com.joust.backend.core.model;

import java.util.UUID;

import org.junit.Test;
import org.meanbean.test.HashCodeMethodTester;
import org.meanbean.util.RandomValueGenerator;
import org.meanbean.util.SimpleRandomValueGenerator;

import com.joust.backend.core.model.ExternalProfileSource.Source;

public class ExternalProfileSourceTest {

  private RandomValueGenerator generator = new SimpleRandomValueGenerator();

  @Test
  public void testHashCode() {
    ExternalProfileSource eps = ExternalProfileSource.builder().referenceId(new String(generator.nextBytes(2)))
        .source(generator.nextBoolean() ? Source.GOOGLE : Source.FACEBOOK).userProfileId(UUID.randomUUID()).build();
    new HashCodeMethodTester().testHashCodeMethod(() -> {
      return eps;
    });
  }

}
