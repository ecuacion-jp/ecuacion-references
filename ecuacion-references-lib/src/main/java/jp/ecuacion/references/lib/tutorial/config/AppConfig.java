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

package jp.ecuacion.references.lib.tutorial.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;

/** Application configuration. */
@Configuration
@ComponentScan(basePackages = "jp.ecuacion.splib.web.config")
public class AppConfig {

  /**
   * Resolves locale from the {@code lang} cookie, then {@code Accept-Language} header.
   *
   * <p>Japanese locale is returned only when the resolved language is {@code "ja"};
   * all other cases resolve to English.</p>
   *
   * @return the locale resolver
   */
  @Bean
  LocaleResolver localeResolver() {
    return new LocaleResolver() {
      @Override
      public Locale resolveLocale(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
          for (Cookie cookie : cookies) {
            if ("lang".equals(cookie.getName())) {
              if ("ja".equals(cookie.getValue())) {
                return Locale.JAPANESE;
              }
              if ("en".equals(cookie.getValue())) {
                return Locale.ENGLISH;
              }
            }
          }
        }
        String acceptLang = request.getHeader("Accept-Language");
        if (acceptLang != null && acceptLang.toLowerCase().startsWith("ja")) {
          return Locale.JAPANESE;
        }
        return Locale.ENGLISH;
      }

      @Override
      @SuppressWarnings("unused")
      public void setLocale(HttpServletRequest rq, HttpServletResponse rs, Locale lc) {
        // Cookie is managed by LanguageController; nothing to do here.
      }
    };
  }
}
