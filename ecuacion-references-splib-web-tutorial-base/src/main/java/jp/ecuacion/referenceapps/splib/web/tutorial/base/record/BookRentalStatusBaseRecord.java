package jp.ecuacion.referenceapps.splib.web.tutorial.base.record;

import jakarta.validation.Valid;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.entity.BookRentalStatus;
import jp.ecuacion.lib.core.annotation.ItemNameKeyClass;
import jp.ecuacion.lib.core.item.*;
import jp.ecuacion.lib.core.util.StringUtil;
import jp.ecuacion.lib.validation.constraints.*;
import jp.ecuacion.splib.core.container.*;

@ItemNameKeyClass("bookRentalStatus")
public abstract class BookRentalStatusBaseRecord extends SystemCommonBaseRecord implements ItemContainer {

  @Valid
  protected BookBaseRecord book;
  @SizeString(min = 1, max = 30)
  @PatternWithDescription(regexp = "^[^!\"#\\$%&\\(\\)=\\^~\\\\\\|`\\[\\{;\\+:\\\\*\\]\\},<>/\\?]*$", description = "prohibitedChars")
  protected String status;

  static {
    getStringLengthMap().put("bookId", null);
    getStringLengthMap().put("status", 30);
  }

  public BookRentalStatusBaseRecord() {
    this(3);
  }

  public BookRentalStatusBaseRecord(int count) {
    super();

    count--;

    if (count > 0) {
      book = new BookBaseRecord() {public Item[] customizedItems() {return null;}};
    }
  }

  public BookRentalStatusBaseRecord(BookRentalStatus e, DatetimeFormatParameters params) {
    this(e, params, 3);
  }

  public BookRentalStatusBaseRecord(BookRentalStatus e, DatetimeFormatParameters params, int count) {
    super(e, params);

    count--;

    if (count > 0) {
      this.book = new BookBaseRecord(e.getBook(), params) {public Item[] customizedItems() {return null;}};
    }
    this.status = e.getStatus();
  }

  public BookRentalStatusBaseRecord(BookRentalStatusBaseRecord rec) {
    this(rec, 3);
  }

  public BookRentalStatusBaseRecord(BookRentalStatusBaseRecord rec, int count) {
    super(rec);

    count--;

    if (count > 0) {
      this.book = new BookBaseRecord() {public Item[] customizedItems() {return null;}};
      this.setBookId(rec.getBookId());
    }
    this.status = rec.getStatus();
  }

  public String getBookId() {
    return book == null ? null : book.getId();
  }

  public void setBookId(String bookId) {
    this.book.setId(bookId);
  }

  public Long getBookIdOfEntityDataType() {
    return (getBookId() == null || getBookId().equals("")) ? null : getBook().getIdOfEntityDataType();
  }

  public BookBaseRecord getBook() {
    return book;
  }

  public void setBook(BookBaseRecord book) {
    this.book = book;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getIds() {
    return StringUtil.getSeparatedValuesString(new String[] {getBook().getId() == null ? "" : getBook().getId(), getBook() == null || getBook().getId() == null? "" : getBook().getId()}, "-");
  }

  public void setIds(String idCsv) {
    String[] ids = idCsv.split("-");
    if (ids.length < 2) return;

    getBook().setId(ids[0]);
    getBook().setId(ids[1]);
  }

  public String getOptimisticLockVersions() {
    return StringUtil.getSeparatedValuesString(new String[] {getVersion() == null ? "" : getVersion(), getBook() == null || getBook().getVersion() == null ? "" : getBook().getVersion()}, "-");
  }

  public void setOptimisticLockVersions(String versionCsv) {
    String[] versions = versionCsv.split("-");
    if (versions.length < 2) return;

    setVersion(versions[0]);
    getBook().setVersion(versions[1]);
  }
}
