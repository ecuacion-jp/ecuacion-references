package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form;

import jakarta.validation.Valid;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record.GreetingInputRecord;
import jp.ecuacion.splib.web.form.SplibGeneralForm;

public class PtGeneralMltForms1CtlInputForm extends SplibGeneralForm {

  @Valid
  private GreetingInputRecord greeting =
      new GreetingInputRecord();

  public GreetingInputRecord getGreeting() {
    return greeting;
  }

  public void setGreeting(GreetingInputRecord greeting) {
    this.greeting = greeting;
  }
}
