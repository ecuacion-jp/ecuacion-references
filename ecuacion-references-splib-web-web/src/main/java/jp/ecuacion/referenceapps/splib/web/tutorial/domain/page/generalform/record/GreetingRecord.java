package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record;

import jp.ecuacion.splib.core.record.SplibRecord;
import jp.ecuacion.splib.web.item.HtmlItem;
import jp.ecuacion.splib.web.item.HtmlItemContainer;

public class GreetingRecord extends SplibRecord implements HtmlItemContainer {

  // private String serverTimestamp;

  private String name;
  private String greetingMessage;

  // public String getServerTimestamp() {
  // return serverTimestamp == null || serverTimestamp == "" ? "（未取得）" : serverTimestamp;
  // }
  //
  // public void setServerTimestamp(String serverTimestamp) {
  // this.serverTimestamp = serverTimestamp;
  // }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getGreetingMessage() {
    return greetingMessage;
  }

  public void setGreetingMessage(String greetingMessage) {
    this.greetingMessage = greetingMessage;
  }

  @Override
  public HtmlItem[] customizedItems() {
    return new HtmlItem[] {new HtmlItem("name").notEmpty()};
  }
}
