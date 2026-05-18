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

package jp.ecuacion.references.lib.tutorial.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Resolves the display language from the request context.
 *
 * <p>Priority: {@code lang} cookie → {@code Accept-Language} header →
 * English fallback for any non-Japanese locale.</p>
 */
final class LangResolver {

  private LangResolver() {}

  /**
   * Returns {@code "ja"} or {@code "en"} based on the request context.
   *
   * @param request the current HTTP request
   * @return resolved language code
   */
  static String resolve(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("lang".equals(cookie.getName())) {
          String val = cookie.getValue();
          if ("ja".equals(val) || "en".equals(val)) {
            return val;
          }
        }
      }
    }
    String acceptLang = request.getHeader("Accept-Language");
    if (acceptLang != null && acceptLang.toLowerCase().startsWith("ja")) {
      return "ja";
    }
    return "en";
  }
}
