package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service;

import jp.ecuacion.lib.core.exception.checked.BizLogicAppException;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneralMltFormsMltCtlsInputForm;
import jp.ecuacion.splib.web.service.SplibGeneralService;
import org.springframework.stereotype.Component;

@Component
public class PtGeneralMltFormsMltCtlsInputService extends SplibGeneralService {

  public void validationCheck(PtGeneralMltFormsMltCtlsInputForm inputForm)
      throws BizLogicAppException {

    String name = inputForm.getGreeting().getName();
    if (!name.contains("X") && !name.contains("Y") && !name.contains("Z")) {
      throw new BizLogicAppException(new String[] {"name"}, 
          "PT_GENERAL_MLT_FORMS_1_CTL_MSG_CHAR_INAPPROPRIATE");
    }
  }
}
