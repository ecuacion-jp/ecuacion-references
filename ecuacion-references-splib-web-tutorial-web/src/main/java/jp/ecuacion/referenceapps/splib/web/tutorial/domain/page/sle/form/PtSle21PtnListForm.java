package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.sle.form;

import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record.MonthRecord;
import jp.ecuacion.splib.web.form.SplibListForm;

public class PtSle21PtnListForm extends SplibListForm<MonthRecord> {

  private MonthRecord month = new MonthRecord();

  public MonthRecord getMonth() {
    return month;
  }

  public void setMonth(MonthRecord month) {
    this.month = month;
  }
}
