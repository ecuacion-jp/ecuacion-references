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

package jp.ecuacion.references.toolcodegenerator.tutorial.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.LocaleResolver;

/** Sets the {@code lang} cookie and redirects to the home page in the chosen language. */
@Controller
@RequestMapping("/public/lang")
public class LangController {

  private final LocaleResolver localeResolver;

  /** Constructor. */
  public LangController(LocaleResolver localeResolver) {
    this.localeResolver = localeResolver;
  }

  /**
   * Switches the display language by updating the locale cookie, then redirects to home.
   *
   * @param lang {@code ja} or {@code en}
   * @param request HTTP request
   * @param response HTTP response
   * @return redirect to the language-specific home page
   */
  @GetMapping("/{lang}")
  public String switchLang(
      @PathVariable String lang,
      HttpServletRequest request,
      HttpServletResponse response) {
    Locale locale = "ja".equals(lang) ? Locale.JAPANESE : Locale.ENGLISH;
    localeResolver.setLocale(request, response, locale);
    return "redirect:/public/" + ("ja".equals(lang) ? "ja" : "en") + "/article?id=home";
  }
}
