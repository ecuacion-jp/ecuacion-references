package jp.ecuacion.referenceapps.splib.web.tutorial.component;

import jp.ecuacion.splib.core.form.record.SplibRecord;
import jp.ecuacion.splib.web.bean.HtmlItem;
import jp.ecuacion.splib.web.form.record.RecordInterface;

public class GreetingWithFirstNameRecord extends SplibRecord implements RecordInterface {

  private String firstName;
  private String message;

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public HtmlItem[] getHtmlItems() {
    return new HtmlItem[] {};
  }

}
