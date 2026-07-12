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

import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneralMltFormsMltCtlsInputForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service.PtGeneralMltFormsMltCtlsInputService;
import jp.ecuacion.splib.web.bean.ReturnUrlBuilder;
import jp.ecuacion.splib.web.controller.SplibGeneralController;
import jp.ecuacion.splib.web.util.SplibLoginStateUtil;
import jp.ecuacion.splib.web.util.SplibSavedModelUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/public/ptGeneralMltFormsMltCtls/input")
public class PtGeneralMltFormsMltCtlsInputController
    extends SplibGeneralController<PtGeneralMltFormsMltCtlsInputService> {

  private static final String forwardSubFunction = "output";

  @Autowired
  private SplibLoginStateUtil loginStateUtil;

  public PtGeneralMltFormsMltCtlsInputController() {
    super("ptGeneralMltFormsMltCtls",
        newContext().subFunction("input").mainRootRecordName("greeting"));
  }

  @GetMapping(value = "page")
  /** Displays page. */
  public String page(Model model, PtGeneralMltFormsMltCtlsInputForm inputForm,
      RedirectAttributes redirectAttributes) {
    prepare(model, inputForm);

    // エラーメッセージがある場合はそれをoutputControllerに引き継ぐ必要があるが、forwardではmodelは引き継がれないため引き継ぎ処理を行う。
    ReturnUrlBuilder bean = ReturnUrlBuilder.forNormalEnd(this, loginStateUtil)
        .toSubFunction(forwardSubFunction).toPage("page").asForward();

    // modelは次画面でも使用するため引き継ぎ処理
    SplibSavedModelUtil.saveToFlash(model, redirectAttributes, true);
    return bean.getUrl();
  }

  @PostMapping(value = "action", params = "greetingBtn")
  public String greeting(Model model, @Validated PtGeneralMltFormsMltCtlsInputForm inputForm,
      BindingResult result, RedirectAttributes redirectAttributes) throws Exception {

    prepare(model, inputForm.validate(result));
    getService().validationCheck(inputForm);

    // modelは次画面でも使用するため引き継ぎ処理
    ReturnUrlBuilder bean = ReturnUrlBuilder.forNormalEnd(this, loginStateUtil)
        .toSubFunction(forwardSubFunction).toPage("action").asForward();
    SplibSavedModelUtil.saveToFlash(model, redirectAttributes, false);
    return bean.getUrl();
  }
}
