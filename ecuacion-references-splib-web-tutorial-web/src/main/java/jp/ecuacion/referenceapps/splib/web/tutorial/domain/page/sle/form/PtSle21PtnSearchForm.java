package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.sle.form;

import jakarta.annotation.Nonnull;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record.MonthRecord;
import jp.ecuacion.splib.web.form.SplibSearchForm;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class PtSle21PtnSearchForm extends SplibSearchForm {

  private MonthRecord month = new MonthRecord();

  @Nonnull
  protected String getDefaultSortItem() {
    return "id";
  }

  public MonthRecord getMonth() {
    return month;
  }

  public void setMonth(MonthRecord month) {
    this.month = month;
  }
}
