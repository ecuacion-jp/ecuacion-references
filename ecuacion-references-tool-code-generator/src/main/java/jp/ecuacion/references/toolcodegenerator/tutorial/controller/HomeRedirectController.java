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

import java.util.Locale;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Redirects to the language-appropriate home page based on the resolved locale. */
@Controller
@RequestMapping("/public/home")
public class HomeRedirectController {

  /**
   * Resolves the current language from the locale context and redirects to the home article.
   *
   * @return redirect URL
   */
  @GetMapping
  public String home() {
    Locale locale = LocaleContextHolder.getLocale();
    String lang = Locale.JAPANESE.getLanguage().equals(locale.getLanguage()) ? "ja" : "en";
    return "redirect:/public/" + lang + "/article?id=home";
  }
}
