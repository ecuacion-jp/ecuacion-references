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
package jp.ecuacion.referenceapps.splib.web.tutorial.base.entity;

import org.jspecify.annotations.NonNull;
import jakarta.persistence.*;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.*;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.record.BookRentalStatusBaseRecord;
import jp.ecuacion.lib.validation.constraints.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "BOOK_RENTAL_STATUS")
public final class BookRentalStatus extends SystemCommon implements Serializable {

  private static final long serialVersionUID = 1L;

  @NotNull
  @Valid
  @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH})
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "BOOK_ID", referencedColumnName = "ID", nullable = false, columnDefinition = "bigint")
  @MapsId
  private Book book = new Book();

  @NotNull
  @Id
  @Column(name = "BOOK_ID", nullable = false)
  protected Long bookId;

  @NotEmpty
  @SizeString(min = 1, max = 30)
  @PatternWithDescription(regexp = "^[^!\"#\\$%&\\(\\)=\\^~\\\\\\|`\\[\\{;\\+:\\\\*\\]\\},<>/\\?]*$", description = "prohibitedChars")
  @Column(name = "STATUS", nullable = false, length = 30)
  protected String status;

  public static final String FIELD_BOOK_ID = "bookId";
  public static final String FIELD_STATUS = "status";

  @Override
  public String[] getFieldNameArr() {
    return new String[] {"bookId", "status", "createAccId", "createTime", "lstUpdAccId", "lstUpdTime", "isDeleted", "version"};
  }

  public BookRentalStatus() {}

  public BookRentalStatus(BookRentalStatusBaseRecord rec, Book book) {
    super(rec);

    if (book != null) setBook(book);
    if (rec.getStatus() != null) setStatus(rec.getStatus());
  }

  public void update(BookRentalStatusBaseRecord rec, Book book, String... skipUpdateFields) {
    List<String> skipUpdateFieldList = Arrays.asList(skipUpdateFields);

    if (book != null) setBook(book);
    if (rec.getStatus() != null && !skipUpdateFieldList.contains(FIELD_STATUS)) setStatus(rec.getStatus());
  }

  public Long getBookId() {
    return book == null ? null : book.getId();
  }

  public void setBookId(Long bookId) {
    this.book.setId(bookId);
  }

  public Book getBook() {
    return book;
  }

  public void setBook(Book book) {
    this.book = book;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<String> getNaturalKeyFieldList() {
    return null;
  }

  @NonNull
  public Set<List<String>> getSetOfUniqueConstraintFieldList() {
    Set<List<String>> rtnSet = new HashSet<>();
    List<String> list = getNaturalKeyFieldList();
    if (list != null) {
      rtnSet.add(list);
    }

    return rtnSet;
  }

}
