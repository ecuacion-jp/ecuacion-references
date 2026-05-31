package jp.ecuacion.referenceapps.splib.web.tutorial.base.config;

import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EntityScan("jp.ecuacion.referenceapps.splib.web.tutorial.base.entity")
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
@ComponentScan("jp.ecuacion.splib.jpa.config"
    + ",jp.ecuacion.referenceapps.splib.web.tutorial.base.advice"
    + ",jp.ecuacion.referenceapps.splib.web.tutorial.base.util"
    )
@PropertySource(value = "classpath:application_base.properties")
public class BaseConfig {

  @Bean
  DateTimeProvider dateTimeProvider() {
    return new DateTimeProvider() {
      @Override
      public Optional<TemporalAccessor> getNow() {
        OffsetDateTime time = OffsetDateTime.now();
        return Optional.of(time);
      }
    };
  }

}
