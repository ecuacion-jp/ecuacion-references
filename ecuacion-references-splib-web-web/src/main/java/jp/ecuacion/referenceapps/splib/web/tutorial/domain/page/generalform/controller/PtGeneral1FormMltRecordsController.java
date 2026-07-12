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
package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.controller;

import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneral1FormMltRecordsForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service.PtGeneral1FormMltRecordsService;
import jp.ecuacion.splib.web.controller.SplibGeneralController;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Scope("prototype")
@RequestMapping("/public/ptGeneral1FormMltRecords")
public class PtGeneral1FormMltRecordsController
    extends SplibGeneralController<PtGeneral1FormMltRecordsService> {

  public PtGeneral1FormMltRecordsController() {
    super("ptGeneral1FormMltRecords");
  }

  @GetMapping("page")
  /** Displays page. */
  public String page(Model model, PtGeneral1FormMltRecordsForm form) {
    prepare(model, form);
    return getDefaultHtmlPageName();
  }

  @PostMapping(value = "action", params = "serverInfoBtn")
  public String serverTimestamp(Model model, PtGeneral1FormMltRecordsForm form,
      RedirectAttributes redirectAttributes) throws Exception {

    prepare(model, form);
    getService().getServerTimestamp(form, request.getLocale());

    // modelは画面でも使用するため引き継ぎ処理
    return redirectToSamePageTakingOverModel(model, true, redirectAttributes);
  }

  @PostMapping(value = "action", params = "greetingBtn")
  public String greeting(Model model, @Validated PtGeneral1FormMltRecordsForm form,
      BindingResult result, RedirectAttributes redirectAttributes) throws Exception {

    prepare(model, form.validate(result));
    getService().getGreeting(form);

    // modelは画面でも使用するため引き継ぎ処理
    return redirectToSamePageTakingOverModel(model, true, redirectAttributes);
  }

}
