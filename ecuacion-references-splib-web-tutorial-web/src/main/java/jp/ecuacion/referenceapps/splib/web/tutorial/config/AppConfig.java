package jp.ecuacion.referenceapps.splib.web.tutorial.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("jp.ecuacion.referenceapps.splib.web.tutorial.repository")
@ComponentScan(basePackages = "jp.ecuacion.splib.web.config"
    + ",jp.ecuacion.referenceapps.splib.web.tutorial.base.config"
    )
public class AppConfig {

  public static final String SESSION_KEY_LOGIN_USER_ID = "loginUserId";

  @Bean
  AuditorAware<Long> auditorAware(HttpServletRequest request) {
    return new AuditorAware<Long>() {
      @Override
      public Optional<Long> getCurrentAuditor() {

        Long accId = (Long) request.getSession().getAttribute(SESSION_KEY_LOGIN_USER_ID);

        // publicでのDB更新もあり、その場合はaccIdが存在しないのでゼロとする
        if (accId == null) {
          accId = 0L;
        }

        return Optional.of(accId);
      }
    };
  }
}
