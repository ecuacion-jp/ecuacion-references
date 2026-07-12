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
package jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.options;

import jakarta.validation.Valid;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.GreetingWithFirstNameRecord;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.options.OptionsController.IntroductionForm;
import jp.ecuacion.splib.web.controller.SplibGeneral1FormController;
import jp.ecuacion.splib.web.form.SplibGeneralForm;
import jp.ecuacion.splib.web.service.SplibGeneral1FormDoNothingService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Scope("prototype")
@RequestMapping("/public/01-component/options/introduction")
//@formatter:off
public class OptionsController extends SplibGeneral1FormController<IntroductionForm, 
    SplibGeneral1FormDoNothingService<IntroductionForm>> {
  //@formatter:on

  public OptionsController() {
    super("introduction",
        newContext().functionKinds("01-component/options").mainRootRecordName("greeting"));
  }

  /**
   * Provides a form for greeting page.
   * 
   * <p>It's okay for this class to be an independent one. 
   *     This is just an saving of class files.</p>
   */
  public static class IntroductionForm extends SplibGeneralForm {

    @Valid
    private GreetingWithFirstNameRecord greeting = new GreetingWithFirstNameRecord("John");

    public GreetingWithFirstNameRecord getGreeting() {
      return greeting;
    }

    public void setGreeting(GreetingWithFirstNameRecord greeting) {
      this.greeting = greeting;
    }
  }
}
