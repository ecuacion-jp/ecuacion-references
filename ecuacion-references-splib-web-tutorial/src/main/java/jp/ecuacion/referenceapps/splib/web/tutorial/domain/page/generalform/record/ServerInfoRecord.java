package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record;

import jp.ecuacion.splib.core.record.SplibRecord;
import jp.ecuacion.splib.web.item.HtmlItem;
import jp.ecuacion.splib.web.item.HtmlItemContainer;

public class ServerInfoRecord extends SplibRecord implements HtmlItemContainer {
  
  private String timestamp;

  public String getTimestamp() {
    return timestamp == null || timestamp.equals("") ? "（未取得）" : timestamp;
  }

  public void setTimestamp(String serverTimestamp) {
    this.timestamp = serverTimestamp;
  }

  @Override
  public HtmlItem[] getHtmlItems() {
    return new HtmlItem[] {};
  }
}
