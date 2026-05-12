package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form;

import jakarta.validation.Valid;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record.GreetingRecord;
import jp.ecuacion.splib.web.form.SplibGeneralForm;

public class PtGeneral1FormSampleForm extends SplibGeneralForm {

  @Valid
  private GreetingRecord greeting = new GreetingRecord();

  public GreetingRecord getGreeting() {
    return greeting;
  }

  public void setGreeting(GreetingRecord greeting) {
    this.greeting = greeting;
  }
}
