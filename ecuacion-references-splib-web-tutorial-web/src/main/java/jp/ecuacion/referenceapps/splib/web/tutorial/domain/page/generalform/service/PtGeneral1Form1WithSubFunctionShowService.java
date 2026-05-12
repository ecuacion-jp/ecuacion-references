package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service;

import jp.ecuacion.lib.core.violation.BusinessViolation;
import jp.ecuacion.lib.core.violation.Violations;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneral1Form1CtlWithSubFunctionInputOutputForm;
import jp.ecuacion.splib.web.service.SplibGeneralService;
import org.springframework.stereotype.Component;

@Component
public class PtGeneral1Form1WithSubFunctionShowService extends SplibGeneralService {

  /** Gets greeting. */
  public void getGreeting(PtGeneral1Form1CtlWithSubFunctionInputOutputForm form) {
    String name = form.getGreeting().getName();

    if (!name.contains("X") && !name.contains("Y") && !name.contains("Z")) {
      new Violations()
          .add(new BusinessViolation("PT_GENERAL_MLT_FORMS_1_CTL_MSG_CHAR_INAPPROPRIATE"))
          .throwIfAny();
    }

    form.getGreeting().setGreetingMessage("Hi, " + form.getGreeting().getName() + "!");
  }
}
