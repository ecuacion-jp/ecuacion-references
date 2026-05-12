package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service;

import jp.ecuacion.lib.core.violation.BusinessViolation;
import jp.ecuacion.lib.core.violation.Violations;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneral1FormSampleForm;
import jp.ecuacion.splib.web.service.SplibGeneral1FormService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class PtGeneral1FormSampleService
    extends SplibGeneral1FormService<PtGeneral1FormSampleForm> {

  @Override
  public void prepareForm(PtGeneral1FormSampleForm form, UserDetails loginUser) {
    
  }

  @Override
  public void page(PtGeneral1FormSampleForm generalForm, UserDetails loginUser) throws Exception {
    // 処理なし
  }

  /** Gets greeting. */
  public void getGreeting(PtGeneral1FormSampleForm form) {

    String name = form.getGreeting().getName();

    if (!name.contains("X") && !name.contains("Y") && !name.contains("Z")) {
      new Violations()
          .add(new BusinessViolation(new String[] {"name"},
              "PT_GENERAL_MLT_FORMS_1_CTL_MSG_CHAR_INAPPROPRIATE"))
          .throwIfAny();
    }

    form.getGreeting().setGreetingMessage("Hi, " + form.getGreeting().getName() + "!");
  }
}
