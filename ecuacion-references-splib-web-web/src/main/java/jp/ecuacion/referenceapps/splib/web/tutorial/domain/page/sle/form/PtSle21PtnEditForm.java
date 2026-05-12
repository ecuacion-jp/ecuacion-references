package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.sle.form;

import jakarta.validation.Valid;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record.MonthRecord;
import jp.ecuacion.splib.web.form.SplibEditForm;

public class PtSle21PtnEditForm extends SplibEditForm {

  @Valid
  private MonthRecord month = new MonthRecord();

  public MonthRecord getMonth() {
    return month;
  }

  public void setMonth(MonthRecord month) {
    this.month = month;
  }
}
