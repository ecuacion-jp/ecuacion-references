package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service;

import jp.ecuacion.lib.core.violation.BusinessViolation;
import jp.ecuacion.lib.core.violation.Violations;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneralMltForms1CtlInputForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneralMltForms1CtlOutputForm;
import jp.ecuacion.splib.web.service.SplibGeneralService;
import org.springframework.stereotype.Component;

@Component
public class PtGeneralMltForms1CtlService extends SplibGeneralService {

  /** Gets greeting. */
  public void getGreeting(PtGeneralMltForms1CtlInputForm form,
      PtGeneralMltForms1CtlOutputForm outputForm) {
    String name = form.getGreeting().getName();

    if (!name.contains("X") && !name.contains("Y") && !name.contains("Z")) {
      new Violations()
          .add(new BusinessViolation("PT_GENERAL_MLT_FORMS_1_CTL_MSG_CHAR_INAPPROPRIATE"))
          .throwIfAny();
    }

    outputForm.getGreeting().setGreetingMessage("Hi, " + name + "!");
  }
}
