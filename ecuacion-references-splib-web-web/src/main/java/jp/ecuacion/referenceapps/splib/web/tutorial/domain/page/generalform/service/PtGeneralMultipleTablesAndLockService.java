/*
 * Copyright © 2012 ecuacion.jp (info@ecuacion.jp)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.entity.Book;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.entity.BookRentalStatus;
import jp.ecuacion.referenceapps.splib.web.tutorial.bl.BookBl;
import jp.ecuacion.referenceapps.splib.web.tutorial.bl.BookRentalStatusBl;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneralMultipleTablesAndLockForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record.BookRentalStatusRecord;
import jp.ecuacion.referenceapps.splib.web.tutorial.repository.BookRentalStatusRepository;
import jp.ecuacion.splib.web.jpa.service.SplibGeneralJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PtGeneralMultipleTablesAndLockService
    extends SplibGeneralJpaService<BookRentalStatus> {

  @Autowired
  private HttpServletRequest request;

  @Autowired
  BookRentalStatusRepository repo;

  @Autowired
  BookRentalStatusBl bl;

  @Autowired
  BookBl bookBl;

  public void page(PtGeneralMultipleTablesAndLockForm form, String function) {
    List<BookRentalStatus> list = repo.findAll();

    // 日本時間固定
    request.getSession().setAttribute("zoneOffset", "-540");
    if (list.size() > 0) {
      // あっても1行なのであれば取得
      BookRentalStatusRecord rec = new BookRentalStatusRecord(list.get(0), getParams());
      form.setBookRentalStatus(rec);
    }
  }

  public void action(PtGeneralMultipleTablesAndLockForm form) throws Exception {
    BookRentalStatusRecord rec = form.getBookRentalStatus();

    List<BookRentalStatus> list = repo.findAll();

    if (list.size() == 0) {
      Book book = bookBl.insertOrUpdate(rec.getBook());
      repo.save(new BookRentalStatus(rec, book));

    } else {
      BookRentalStatus e = bl.findAndOptimisticLockingCheck(rec);
      e.setStatus(rec.getStatus());
      e.getBook().setName(rec.getBook().getName());
    }
  }
}
