package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service;

import jp.ecuacion.lib.core.exception.checked.BizLogicAppException;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneralMltFormsMltCtlsOutputForm;
import jp.ecuacion.splib.web.service.SplibGeneralService;
import org.springframework.stereotype.Component;

@Component
public class PtGeneralMultipleFormsMultipleCtlsOutputService extends SplibGeneralService {

  public void getGreeting(PtGeneralMltFormsMltCtlsOutputForm outputForm)
      throws BizLogicAppException {
    String name = outputForm.getGreeting().getName();

    if (name != null && !name.equals("")) {
      outputForm.getGreeting().setGreetingMessage("Hi, " + name + "!");

    } else {
      outputForm.getGreeting().setGreetingMessage("");
    }
  }
}
