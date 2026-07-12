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
package jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.htmlitem;

import jakarta.validation.Valid;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.htmlitem.ItemNameKeyController.ItemNameKeyForm;
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
@RequestMapping("/public/01-component/htmlItem/itemNameKey")
//@formatter:off
public class ItemNameKeyController extends SplibGeneral1FormController<ItemNameKeyForm, 
    SplibGeneral1FormDoNothingService<ItemNameKeyForm>> {
  //@formatter:on

  public ItemNameKeyController() {
    super("itemNameKey",
        newContext().functionKinds("01-component", "htmlItem").mainRootRecordName("greeting"));
  }

  @PostMapping(value = "action", params = "greetingButton")
  public String greeting(Model model, @Validated ItemNameKeyForm form, BindingResult result,
      RedirectAttributes redirectAttributes) throws Exception {
    prepare(model, form.validate(result));

    return redirectToSamePageTakingOverModel(model, true, redirectAttributes);
  }

  /**
   * Provides a form for greeting page.
   * 
   * <p>It's okay for this class to be an independent one. 
   *     This is just an saving of class files.</p>
   */
  public static class ItemNameKeyForm extends SplibGeneralForm {

    @Valid
    private MyRecord greeting = new MyRecord();

    public MyRecord getGreeting() {
      return greeting;
    }

    public void setGreeting(MyRecord greeting) {
      this.greeting = greeting;
    }
  }

  public static class MyRecord extends SplibRecord implements HtmlItemContainer {

    private String anotherFirstName1;
    private String anotherFirstName2;

    public void setAnotherFirstName1(String anotherFirstName1) {
      this.anotherFirstName1 = anotherFirstName1;
    }

    public String getAnotherFirstName1() {
      return anotherFirstName1;
    }

    public void setAnotherFirstName2(String anotherFirstName2) {
      this.anotherFirstName2 = anotherFirstName2;
    }

    public String getAnotherFirstName2() {
      return anotherFirstName2;
    }

    @Override
    public HtmlItem[] customizedItems() {
      return new HtmlItem[] {new HtmlItem("anotherFirstName1").itemNameKey("greeting.firstName"),
          new HtmlItem("anotherFirstName2").itemNameKey("explanatoryFirstName")};
    }
  }
}
