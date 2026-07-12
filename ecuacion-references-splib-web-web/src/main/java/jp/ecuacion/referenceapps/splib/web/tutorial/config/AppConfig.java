/*
 * Copyright © 2012 ecuacion.jp (info@ecuacion.jp)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
