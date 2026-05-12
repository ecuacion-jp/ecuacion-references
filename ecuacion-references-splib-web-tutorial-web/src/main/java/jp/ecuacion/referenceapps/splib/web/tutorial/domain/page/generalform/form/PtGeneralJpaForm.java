package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form;

import jakarta.validation.Valid;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record.YourNameRecord;
import jp.ecuacion.splib.web.form.SplibGeneralForm;

public class PtGeneralJpaForm extends SplibGeneralForm {

  @Valid
  YourNameRecord yourName = new YourNameRecord();

  public YourNameRecord getYourName() {
    return yourName;
  }

  public void setYourName(YourNameRecord yourName) {
    this.yourName = yourName;
  }

}
