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
package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service;

import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneralMltFormsMltCtlsOutputForm;
import jp.ecuacion.splib.web.service.SplibGeneralService;
import org.springframework.stereotype.Component;

@Component
public class PtGeneralMultipleFormsMultipleCtlsOutputService extends SplibGeneralService {

  /** Gets greeting. */
  public void getGreeting(PtGeneralMltFormsMltCtlsOutputForm outputForm) {
    String name = outputForm.getGreeting().getName();

    if (name != null && !name.equals("")) {
      outputForm.getGreeting().setGreetingMessage("Hi, " + name + "!");

    } else {
      outputForm.getGreeting().setGreetingMessage("");
    }
  }
}
