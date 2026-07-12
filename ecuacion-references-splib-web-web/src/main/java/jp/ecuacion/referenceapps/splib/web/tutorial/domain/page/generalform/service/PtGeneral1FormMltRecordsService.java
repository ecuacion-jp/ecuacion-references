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

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import jp.ecuacion.lib.core.violation.BusinessViolation;
import jp.ecuacion.lib.core.violation.Violations;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneral1FormMltRecordsForm;
import jp.ecuacion.splib.web.service.SplibGeneralService;
import org.springframework.stereotype.Component;

@Component
public class PtGeneral1FormMltRecordsService extends SplibGeneralService {

  public void getServerTimestamp(PtGeneral1FormMltRecordsForm form, Locale locale) {
    TimeZone tz = Calendar.getInstance(locale).getTimeZone();
    OffsetDateTime offsetDateTime = ZonedDateTime.now(tz.toZoneId()).toOffsetDateTime();
    form.getServerInfo().setTimestamp(offsetDateTime.toString());
  }

  /** Gets greeting. */
  public void getGreeting(PtGeneral1FormMltRecordsForm form) {
    String name = form.getGreeting().getName();

    if (!name.contains("X") && !name.contains("Y") && !name.contains("Z")) {
      new Violations()
          .add(new BusinessViolation("PT_GENERAL_MLT_FORMS_1_CTL_MSG_CHAR_INAPPROPRIATE"))
          .throwIfAny();
    }

    form.getGreeting().setGreetingMessage("Hi, " + form.getGreeting().getName() + "!");
  }
}
