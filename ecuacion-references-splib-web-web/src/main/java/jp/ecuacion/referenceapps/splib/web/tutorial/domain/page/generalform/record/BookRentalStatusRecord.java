package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record;

import jp.ecuacion.referenceapps.splib.web.tutorial.base.entity.BookRentalStatus;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.record.BookRentalStatusBaseRecord;
import jp.ecuacion.splib.core.container.DatetimeFormatParameters;
import jp.ecuacion.splib.web.item.HtmlItem;
import jp.ecuacion.splib.web.item.HtmlItemContainer;

public class BookRentalStatusRecord extends BookRentalStatusBaseRecord implements HtmlItemContainer {

  private String versions;

  public BookRentalStatusRecord() {
    super();
  }

  public BookRentalStatusRecord(BookRentalStatus e, DatetimeFormatParameters params) {
    super(e, params);
  }

  @Override
  public HtmlItem[] customizedItems() {
    return new HtmlItem[] {new HtmlItem("status").notEmpty(),
        new HtmlItem("book.name").notEmpty()};
  }

  public String getVersions() {
    return versions;
  }

  public void setVersions(String versions) {
    this.versions = versions;
  }

}
