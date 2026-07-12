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
package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form;

import jakarta.validation.Valid;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record.GreetingRecord;
import jp.ecuacion.splib.web.form.SplibGeneralForm;

public class PtGeneralMltFormsMltCtlsOutputForm extends SplibGeneralForm {

  @Valid
  private GreetingRecord greeting = new GreetingRecord();

  public GreetingRecord getGreeting() {
    return greeting;
  }

  public void setGreeting(GreetingRecord greeting) {
    this.greeting = greeting;
  }

}
