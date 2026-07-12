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
package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.base.controller;

import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.base.form.PtBaseBsBgGradientForm;
import jp.ecuacion.splib.web.controller.SplibGeneralController;
import jp.ecuacion.splib.web.service.SplibGeneralDoNothingService;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Scope("prototype")
@RequestMapping("/public/ptBaseBsBgGradient")
public class PtBaseBsBgGradientController
    extends SplibGeneralController<SplibGeneralDoNothingService> {

  public PtBaseBsBgGradientController() {
    super("ptBaseBsBgGradient", newContext().functionKinds("02-page"));
  }

  @GetMapping("page")
  public String page(Model model, PtBaseBsBgGradientForm form,
      @AuthenticationPrincipal UserDetails loginUser) throws Exception {

    prepare(model, loginUser, form);
    return getDefaultHtmlPageName();
  }

  @PostMapping(value = "action", params = "successMsg")
  public String successMsg(Model model, PtBaseBsBgGradientForm form,
      @AuthenticationPrincipal UserDetails loginUser) throws Exception {

    prepare(model, loginUser, form);
    return getRedirectUrlOnSuccess();
  }
}
