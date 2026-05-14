package your.company.app.record;

import jp.ecuacion.splib.core.record.SplibRecord;
import jp.ecuacion.splib.web.item.HtmlItem;
import jp.ecuacion.splib.web.item.HtmlItemContainer;

public class GreetingRecord extends SplibRecord implements HtmlItemContainer {

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
  public HtmlItem[] customizedItems() {
    return new HtmlItem[] {};
  }
}
