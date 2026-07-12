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

import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneralMltForms1CtlInputForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneralMltForms1CtlOutputForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service.PtGeneralMltForms1CtlService;
import jp.ecuacion.splib.web.controller.SplibGeneralController;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 1 controllerに複数formがある場合は難しいのだが、 generic
 * parameterのformは現時点ではinputよりの機能（submitOnChangeToRefresh）に使用されていることから inputFormを渡しておく。
 */
@Controller
@Scope("prototype")
@RequestMapping("/public/ptGeneralMltForms1Ctl")
public class PtGeneralMltForms1CtlController
    extends SplibGeneralController<PtGeneralMltForms1CtlService> {

  public PtGeneralMltForms1CtlController() {
    super("ptGeneralMltForms1Ctl", newContext().mainRootRecordName("greeting"));
  }

  @GetMapping(value = "page")
  /** Displays page. */
  public String page(Model model, PtGeneralMltForms1CtlInputForm inputForm) {
    prepare(model, inputForm, new PtGeneralMltForms1CtlOutputForm());
    return getDefaultHtmlPageName();
  }

  @PostMapping(value = "action", params = "greetingBtn")
  public String greeting(Model model, @Validated PtGeneralMltForms1CtlInputForm inputForm,
      BindingResult result) throws Exception {
    PtGeneralMltForms1CtlOutputForm outputForm = new PtGeneralMltForms1CtlOutputForm();
    prepare(model, inputForm.validate(result), outputForm);
    getService().getGreeting(inputForm, outputForm);

    return getRedirectUrlOnSuccess();
  }

}
