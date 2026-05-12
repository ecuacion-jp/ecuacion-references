package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form;

import jakarta.validation.Valid;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record.BookRentalStatusRecord;
import jp.ecuacion.splib.web.form.SplibGeneralForm;

public class PtGeneralMultipleTablesAndLockForm extends SplibGeneralForm {

  @Valid
  BookRentalStatusRecord bookRentalStatus = new BookRentalStatusRecord();

  public BookRentalStatusRecord getBookRentalStatus() {
    return bookRentalStatus;
  }

  public void setBookRentalStatus(BookRentalStatusRecord bookRentalStatus) {
    this.bookRentalStatus = bookRentalStatus;
  }

}
