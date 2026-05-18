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

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Adds {@code currentLang} to every model so Thymeleaf templates can build language-aware URLs. */
@ControllerAdvice
public class LangModelAdvice {

  /**
   * Resolves the current display language and exposes it as a model attribute.
   *
   * @param request the current HTTP request
   * @return {@code "ja"} or {@code "en"}
   */
  @ModelAttribute("currentLang")
  public String currentLang(HttpServletRequest request) {
    return LangResolver.resolve(request);
  }
}
