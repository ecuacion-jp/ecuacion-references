package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record;

import jp.ecuacion.splib.core.form.record.SplibRecord;
import jp.ecuacion.splib.web.bean.HtmlItem;
import jp.ecuacion.splib.web.form.record.RecordInterface;

public class ServerInfoRecord extends SplibRecord implements RecordInterface {
  
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
