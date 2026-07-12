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
package jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.components.generalformpage;

import jakarta.validation.Valid;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.components.generalformpage.ButtonController.ButtonForm;
import jp.ecuacion.splib.core.record.SplibRecord;
import jp.ecuacion.splib.web.controller.SplibGeneral1FormController;
import jp.ecuacion.splib.web.form.SplibGeneralForm;
import jp.ecuacion.splib.web.item.HtmlItem;
import jp.ecuacion.splib.web.item.HtmlItemContainer;
import jp.ecuacion.splib.web.service.SplibGeneral1FormDoNothingService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Scope("prototype")
@RequestMapping("/public/01-component/components/general-form-page/button")
public class ButtonController
    extends SplibGeneral1FormController<ButtonForm, SplibGeneral1FormDoNothingService<ButtonForm>> {

  public ButtonController() {
    super("button", newContext().functionKinds("01-component", "components", "general-form-page"));
  }

  @PostMapping(value = "action", params = "button")
  public String execute(Model model, @Validated ButtonForm form, BindingResult result,
      RedirectAttributes redirectAttributes) throws Exception {

    return redirectToSamePageTakingOverModel(model, true, redirectAttributes);
  }

  public static class ButtonForm extends SplibGeneralForm {

    @Valid
    private ButtonRecord button = new ButtonRecord();

    public ButtonRecord getButton() {
      return button;
    }

    public void setButton(ButtonRecord input) {
      this.button = input;
    }
  }

  public static class ButtonRecord extends SplibRecord implements HtmlItemContainer {

    @Override
    public HtmlItem[] customizedItems() {
      return new HtmlItem[] {};
    }
  }
}
