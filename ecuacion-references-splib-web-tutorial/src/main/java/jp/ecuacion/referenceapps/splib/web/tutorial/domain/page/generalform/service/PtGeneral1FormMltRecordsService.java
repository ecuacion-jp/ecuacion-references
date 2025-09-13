package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import jp.ecuacion.lib.core.exception.checked.BizLogicAppException;
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

  public void getGreeting(PtGeneral1FormMltRecordsForm form)
      throws BizLogicAppException {
    String name = form.getGreeting().getName();

    if (!name.contains("X") && !name.contains("Y") && !name.contains("Z")) {
      throw new BizLogicAppException(
          "PT_GENERAL_MLT_FORMS_1_CTL_MSG_CHAR_INAPPROPRIATE");
    }

    form.getGreeting().setGreetingMessage("Hi, " + form.getGreeting().getName() + "!");
  }
}
