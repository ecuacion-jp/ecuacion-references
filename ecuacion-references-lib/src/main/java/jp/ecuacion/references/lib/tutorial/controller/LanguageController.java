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
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Handles language switching and language-aware home redirect. */
@Controller
public class LanguageController {

  /**
   * Saves the selected language in a persistent cookie and redirects to the home article.
   *
   * @param lang     {@code "ja"} or {@code "en"}; any other value is treated as {@code "en"}
   * @param response the current HTTP response
   * @return redirect to the home article in the selected language
   */
  @GetMapping("/public/lang/change")
  public String changeLanguage(@RequestParam String lang,
      @RequestParam(defaultValue = "home") String returnId,
      HttpServletResponse response) {
    String safeLang = "ja".equals(lang) ? "ja" : "en";
    Cookie cookie = new Cookie("lang", safeLang);
    cookie.setMaxAge(365 * 24 * 60 * 60);
    cookie.setPath("/");
    response.addCookie(cookie);
    String safeId = returnId.matches("[a-zA-Z0-9][a-zA-Z0-9\\-]*(/[a-zA-Z0-9][a-zA-Z0-9\\-]*)*")
        ? returnId : "home";
    return "redirect:/public/" + safeLang + "/article?id=" + safeId;
  }

  /**
   * Redirects to the home article in the language resolved from the request context.
   *
   * @param request the current HTTP request
   * @return redirect to the language-appropriate home article
   */
  @GetMapping("/public/home")
  public String home(HttpServletRequest request) {
    String lang = LangResolver.resolve(request);
    return "redirect:/public/" + lang + "/article?id=home";
  }
}
