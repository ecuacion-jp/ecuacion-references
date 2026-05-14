package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record;

import jp.ecuacion.referenceapps.splib.web.tutorial.base.record.BookBaseRecord;
import jp.ecuacion.splib.web.item.HtmlItem;
import jp.ecuacion.splib.web.item.HtmlItemContainer;

public class BookRecord extends BookBaseRecord implements HtmlItemContainer {

  @Override
  public HtmlItem[] customizedItems() {
    return new HtmlItem[] {
    };
  }
}
