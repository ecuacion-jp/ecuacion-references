package jp.ecuacion.referenceapps.splib.web.tutorial.domain.component;

import jp.ecuacion.splib.core.record.SplibRecord;
import jp.ecuacion.splib.web.item.HtmlItem;
import jp.ecuacion.splib.web.item.HtmlItemContainer;

public class GreetingWithFirstNameRecord extends SplibRecord implements HtmlItemContainer {

  public GreetingWithFirstNameRecord() {

  }

  public GreetingWithFirstNameRecord(String firstName) {
    this.firstName = firstName;
  }

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
