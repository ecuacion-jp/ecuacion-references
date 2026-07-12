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

import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneralMultipleTablesAndLockForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service.PtGeneralMultipleTablesAndLockService;
import jp.ecuacion.splib.web.bean.ReturnUrlBuilder;
import jp.ecuacion.splib.web.controller.SplibGeneralController;
import jp.ecuacion.splib.web.util.SplibLoginStateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Scope("prototype")
@RequestMapping("/public/ptGeneralMultipleTablesAndLock")
public class PtGeneralMultipleTablesAndLockController
    extends SplibGeneralController<PtGeneralMultipleTablesAndLockService> {

  @Autowired
  private SplibLoginStateUtil loginStateUtil;

  public PtGeneralMultipleTablesAndLockController() {
    super("ptGeneralMultipleTablesAndLock",
        newContext().mainRootRecordName("bookRentalStatus").functionKinds("02-page/general-form"));
  }

  @GetMapping("page")
  public String page(Model model) throws Exception {
    PtGeneralMultipleTablesAndLockForm form = new PtGeneralMultipleTablesAndLockForm();
    prepare(model, form);
    getService().page(form, getFunction());
    return getDefaultHtmlPageName();
  }

  @PostMapping(value = "action", params = "update")
  public String update(Model model, @Validated PtGeneralMultipleTablesAndLockForm form,
      BindingResult result) throws Exception {

    prepare(model, form.validate(result));
    getService().action(form);

    return ReturnUrlBuilder.forNormalEnd(this, loginStateUtil).showSuccessMessage().getUrl();
  }
}
