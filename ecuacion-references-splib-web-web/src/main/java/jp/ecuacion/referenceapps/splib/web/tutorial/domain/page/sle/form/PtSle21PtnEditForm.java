package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.sle.form;

import jakarta.validation.Valid;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record.MonthEditRecord;
import jp.ecuacion.splib.web.form.SplibEditForm;

public class PtSle21PtnEditForm extends SplibEditForm {

  @Valid
  private MonthEditRecord month = new MonthEditRecord();

  public MonthEditRecord getMonth() {
    return month;
  }

  public void setMonth(MonthEditRecord month) {
    this.month = month;
  }
}
