package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record;

import jp.ecuacion.referenceapps.splib.web.tutorial.base.entity.YourName;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.record.YourNameBaseRecord;
import jp.ecuacion.splib.core.container.DatetimeFormatParameters;
import jp.ecuacion.splib.web.item.HtmlItem;
import jp.ecuacion.splib.web.item.HtmlItemString;
import jp.ecuacion.splib.web.item.HtmlItemContainer;

public class YourNameRecord extends YourNameBaseRecord implements HtmlItemContainer {

  public YourNameRecord() {
    super();
  }

  public YourNameRecord(YourName e, DatetimeFormatParameters params) {
    super(e, params);
  }

  @Override
  public HtmlItem[] customizedItems() {
    return new HtmlItem[] {new HtmlItemString("validationTest").notEmpty()};
  }
}
