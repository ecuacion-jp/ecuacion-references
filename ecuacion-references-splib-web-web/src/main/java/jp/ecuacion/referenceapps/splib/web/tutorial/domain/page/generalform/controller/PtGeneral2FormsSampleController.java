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

import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneral2FormsSampleInputForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneral2FormsSampleOutputForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service.PtGeneral2FormsSampleService;
import jp.ecuacion.splib.web.controller.SplibGeneral2FormsController;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//@formatter:off
@Controller
@Scope("prototype")
@RequestMapping("/public/ptGeneral2FormsSample")
public class PtGeneral2FormsSampleController extends
    SplibGeneral2FormsController<PtGeneral2FormsSampleInputForm, 
    PtGeneral2FormsSampleOutputForm, PtGeneral2FormsSampleService> {
  //@formatter:on

  public PtGeneral2FormsSampleController() {
    super("ptGeneral2FormsSample", newContext().mainRootRecordName("greeting"));
  }

  @PostMapping(value = "action", params = "greetingBtn")
  public String greeting(Model model, @Validated PtGeneral2FormsSampleInputForm inputForm,
      BindingResult result, RedirectAttributes redirectAttributes) throws Exception {
    PtGeneral2FormsSampleOutputForm outputForm = new PtGeneral2FormsSampleOutputForm();
    prepare(model, inputForm.validate(result), outputForm);
    getService().getGreeting(inputForm, outputForm);

    // modelは画面でも使用するため引き継ぎ処理
    return redirectToSamePageTakingOverModel(model, true, redirectAttributes);
  }

}
