package your.company.app.record;

import jp.ecuacion.splib.core.record.SplibRecord;
import jp.ecuacion.splib.web.bean.HtmlItem;
import jp.ecuacion.splib.web.form.record.RecordInterface;

public class GreetingRecord extends SplibRecord implements RecordInterface {

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
