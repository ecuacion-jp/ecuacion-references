package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record;

import jp.ecuacion.splib.core.record.SplibRecord;
import jp.ecuacion.splib.web.bean.HtmlItem;
import jp.ecuacion.splib.web.form.record.RecordInterface;

public class GreetingInputRecord extends SplibRecord implements RecordInterface {

  private String name;

  static {
    getStringLengthMap().put("name", 20);
  }
  
  public GreetingInputRecord() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public HtmlItem[] getHtmlItems() {
    return new HtmlItem[] {new HtmlItem("name").notEmpty()};
  }
}
