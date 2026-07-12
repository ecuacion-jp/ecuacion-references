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
package jp.ecuacion.referenceapps.splib.web.tutorial.domain.system;

import jakarta.validation.Valid;
import jp.ecuacion.splib.core.record.SplibRecord;
import jp.ecuacion.splib.web.controller.SplibGeneralController;
import jp.ecuacion.splib.web.form.SplibGeneralForm;
import jp.ecuacion.splib.web.item.HtmlItem;
import jp.ecuacion.splib.web.item.HtmlItemContainer;
import jp.ecuacion.splib.web.service.SplibGeneralDoNothingService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Scope("prototype")
@RequestMapping("/public/03-system/stCommonFeatures")
public class StCommonFeaturesController
    extends SplibGeneralController<SplibGeneralDoNothingService> {

  public StCommonFeaturesController() {
    super("stCommonFeatures", newContext().functionKinds("03-system"));
  }

  @GetMapping("page")
  /** Displays page. */
  public String page(Model model) {
    prepare(model, new StCommonFeaturesForm());
    return getDefaultHtmlPageName();
  }

  @GetMapping(value = "action", params = "404")
  public String method404(Model model) {

    return "redirect:/public/notFound/page";
  }

  @GetMapping(value = "action", params = "accessDenied")
  public String config(Model model) {

    return "redirect:/account/loginHome/page";
  }
  
  public static class StCommonFeaturesForm extends SplibGeneralForm {

    @Valid
    private StCommonFeaturesRecord stCommonFunctions = new StCommonFeaturesRecord();

    public StCommonFeaturesRecord getStCommonFunctions() {
      return stCommonFunctions;
    }

    public void setStCommonFunctions(StCommonFeaturesRecord stCommonFunctions) {
      this.stCommonFunctions = stCommonFunctions;
    }
  }
  
  public static class StCommonFeaturesRecord extends SplibRecord implements HtmlItemContainer {

    @Override
    public HtmlItem[] customizedItems() {
      return new HtmlItem[] {};
    }

  }

}
