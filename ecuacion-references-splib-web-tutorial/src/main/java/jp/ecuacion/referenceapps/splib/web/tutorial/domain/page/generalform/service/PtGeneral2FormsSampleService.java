package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service;

import jp.ecuacion.lib.core.exception.checked.BizLogicAppException;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneral2FormsSampleInputForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneral2FormsSampleOutputForm;
import jp.ecuacion.splib.web.service.SplibGeneral2FormsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

//@formatter:off
@Component
public class PtGeneral2FormsSampleService extends SplibGeneral2FormsService
    <PtGeneral2FormsSampleInputForm, PtGeneral2FormsSampleOutputForm> {
  //@formatter:on

  @Override
  public void prepareForm(PtGeneral2FormsSampleInputForm form1,
      PtGeneral2FormsSampleOutputForm form2, UserDetails loginUser) {}

  @Override
  public void page(PtGeneral2FormsSampleInputForm form1, PtGeneral2FormsSampleOutputForm form2,
      UserDetails loginUser) throws Exception {
    // 処理なし

  }

  public void getGreeting(PtGeneral2FormsSampleInputForm inputForm,
      PtGeneral2FormsSampleOutputForm outputForm) throws BizLogicAppException {
    String name = inputForm.getGreeting().getName();

    if (!name.contains("X") && !name.contains("Y") && !name.contains("Z")) {
      throw new BizLogicAppException("PT_GENERAL_MLT_FORMS_1_CTL_MSG_CHAR_INAPPROPRIATE");
    }

    outputForm.getGreeting().setGreetingMessage("Hi, " + name + "!");
  }
}
