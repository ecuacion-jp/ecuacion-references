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

import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneralMltFormsMltCtlsOutputForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service.PtGeneralMultipleFormsMultipleCtlsOutputService;
import jp.ecuacion.splib.web.bean.ReturnUrlBuilder;
import jp.ecuacion.splib.web.controller.SplibGeneralController;
import jp.ecuacion.splib.web.util.SplibLoginStateUtil;
import jp.ecuacion.splib.web.util.SplibSavedModelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Scope("prototype")
@RequestMapping("/public/ptGeneralMltFormsMltCtls/output")
public class PtGeneralMltFormsMltCtlsOutputController
    extends SplibGeneralController<PtGeneralMultipleFormsMultipleCtlsOutputService> {

  @Autowired
  private SplibLoginStateUtil loginStateUtil;

  public PtGeneralMltFormsMltCtlsOutputController() {
    super("ptGeneralMltFormsMltCtls",
        newContext().subFunction("output").htmlFilenamePostfix("").mainRootRecordName("greeting"));
  }

  @RequestMapping(value = "page", method = {RequestMethod.POST, RequestMethod.GET})
  public String page(Model model, PtGeneralMltFormsMltCtlsOutputForm outputForm) throws Exception {

    prepare(model, outputForm);

    // エラーがある場合は処理結果を表示しない
    BindingResult br = (BindingResult) model.getAttribute(
        BindingResult.MODEL_KEY_PREFIX + "ptGeneralMltFormsMltCtlsOutputForm");
    if (br == null || !br.hasErrors()) {
      getService().getGreeting(outputForm);
    }

    model.addAttribute("ptGeneralMltFormsMltCtlsOutputForm", outputForm);

    return getDefaultHtmlPageName();
  }

  @RequestMapping(value = "action", params = "forwarded",
      method = {RequestMethod.POST, RequestMethod.GET})
  public String action(Model model, PtGeneralMltFormsMltCtlsOutputForm outputForm,
      RedirectAttributes redirectAttributes) throws Exception {

    prepare(model, outputForm);
    // データ更新などの処理は特にないので、redirectするのみとする
    // modelは次画面でも使用するため引き継ぎ処理
    ReturnUrlBuilder bean = ReturnUrlBuilder.forNormalEnd(this, loginStateUtil)
        .toSubFunction("input").toPage("page").showSuccessMessage()
        .putParam("greeting.name", outputForm.getGreeting().getName());
    SplibSavedModelUtil.saveToFlash(model, redirectAttributes, false);
    return bean.getUrl();
  }
}
