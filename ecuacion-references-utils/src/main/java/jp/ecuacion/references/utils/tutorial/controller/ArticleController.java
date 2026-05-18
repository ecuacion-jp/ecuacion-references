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

package jp.ecuacion.references.utils.tutorial.controller;

import jp.ecuacion.references.utils.tutorial.service.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Controller for Markdown-based article pages. */
@Controller
@RequestMapping("/public/{lang}/article")
public class ArticleController {

  private final ArticleService articleService;

  /** Constructor. */
  public ArticleController(ArticleService articleService) {
    this.articleService = articleService;
  }

  /**
   * Shows the article page for the given language and article ID.
   *
   * @param lang the language code ({@code ja} or {@code en})
   * @param id the article identifier
   * @param model the Spring MVC model
   * @return template name
   */
  @GetMapping
  public String showArticle(
      @PathVariable String lang,
      @RequestParam String id,
      Model model) {
    model.addAttribute("content", articleService.renderArticle(lang, id));
    return "article";
  }
}
