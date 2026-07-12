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
package jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.basics;

import jakarta.validation.Valid;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.GreetingWithFirstNameRecord;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.basics.AdditionOfTextboxController.AdditionOfTextboxForm;
import jp.ecuacion.splib.web.controller.SplibGeneral1FormController;
import jp.ecuacion.splib.web.form.SplibGeneralForm;
import jp.ecuacion.splib.web.service.SplibGeneral1FormDoNothingService;
import org.apache.groovy.parser.antlr4.util.StringUtils;
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
@RequestMapping("/public/01-component/basics/additionOfTextbox")
//@formatter:off
public class AdditionOfTextboxController extends SplibGeneral1FormController<AdditionOfTextboxForm, 
    SplibGeneral1FormDoNothingService<AdditionOfTextboxForm>> {
  //@formatter:on

  public AdditionOfTextboxController() {
    super("additionOfTextbox",
        newContext().functionKinds("01-component", "basics").mainRootRecordName("greeting"));
  }

  @PostMapping(value = "action", params = "greetingButton")
  public String greeting(Model model, @Validated AdditionOfTextboxForm form, BindingResult result,
      RedirectAttributes redirectAttributes) throws Exception {
    prepare(model, form.validate(result));

    String fn = form.getGreeting().getFirstName();
    boolean fnIsEmpty = StringUtils.isEmpty(fn);
    String ln = form.getGreeting().getLastName();
    boolean lnIsEmpty = StringUtils.isEmpty(ln);
    String fullName =
        (fnIsEmpty ? "" : fn + (!fnIsEmpty && !lnIsEmpty ? " " : "")) + (lnIsEmpty ? "" : ln);
    String msg = fnIsEmpty && lnIsEmpty ? "Hi!" : String.format("Hi, %s!", fullName);
    form.getGreeting().setMessage(msg);

    return redirectToSamePageTakingOverModel(model, true, redirectAttributes);
  }

  /**
   * Provides a form for greeting page.
   * 
   * <p>It's okay for this class to be an independent one. 
   *     This is just an saving of class files.</p>
   */
  public static class AdditionOfTextboxForm extends SplibGeneralForm {

    @Valid
    private GreetingWithFullNameRecord greeting = new GreetingWithFullNameRecord();

    public GreetingWithFullNameRecord getGreeting() {
      return greeting;
    }

    public void setGreeting(GreetingWithFullNameRecord greeting) {
      this.greeting = greeting;
    }
  }


  public static class GreetingWithFullNameRecord extends GreetingWithFirstNameRecord {

    private String lastName;

    public String getLastName() {
      return lastName;
    }

    public void setLastName(String lastName) {
      this.lastName = lastName;
    }
  }
}
