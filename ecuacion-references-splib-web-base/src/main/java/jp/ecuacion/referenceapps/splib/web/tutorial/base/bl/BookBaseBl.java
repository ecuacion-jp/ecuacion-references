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
package jp.ecuacion.referenceapps.splib.web.tutorial.base.bl;

import java.util.Arrays;
import java.util.List;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.entity.*;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.record.*;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.repository.*;
import jp.ecuacion.splib.jpa.repository.SplibRepository;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BookBaseBl extends SystemCommonBaseBl<Book, Long> {

  @Autowired
  protected BookBaseRepository repo;

  @Autowired
  protected BookRentalStatusBaseRepository bookRentalStatusRepo;

  @Override
  public SplibRepository<Book, Long> getRepositoryForOptimisticLocking() {
    return repo;
  }

  public Book findAndOptimisticLockingCheck(BookBaseRecord rec) {
    return findAndOptimisticLockingCheck(rec.getIdOfEntityDataType(), rec.getVersionOfEntityDataType());
  }

  public Book insertOrUpdate(BookBaseRecord rec, String... skipUpdateFields) {
    Book e = null;
    boolean isInsert = rec.getId() == null || rec.getId().equals("");

    if (isInsert) {
      e = new Book(rec);

    } else {
      e = findAndOptimisticLockingCheck(rec);
      e.update(rec, skipUpdateFields);
    }

    return repo.save(e);
  }

  private void duplicateCheck(boolean isCheckFromAllGroups, List<Book> entityList, BookBaseRecord rec, String... targetItemPropertyPaths) {
    internalDuplicateCheck(isCheckFromAllGroups, entityList, rec, "book", "id", targetItemPropertyPaths);
  }

  public void duplicateCheck(List<Book> entityList, BookBaseRecord rec, String... targetItemPropertyPaths) {
    duplicateCheck(false, entityList, rec, targetItemPropertyPaths);
  }

  public void duplicateCheck(BookBaseRecord rec, String... targetItemPropertyPaths) {
    duplicateCheck(repo.findAll(), rec, targetItemPropertyPaths);
  }

  public void duplicateCheckFromAllGroups(List<Book> entityList, BookBaseRecord rec, String... targetItemPropertyPaths) {
    duplicateCheck(true, entityList, rec, targetItemPropertyPaths);
  }

  public void duplicateCheckFromAllGroups(BookBaseRecord rec, String... targetItemPropertyPaths) {
    duplicateCheckFromAllGroups(repo.findAllFromAllGroups(), rec, targetItemPropertyPaths);
  }

  public void childExistenceCheckBookRentalStatus(Long id) {
    childExistenceCheckBookRentalStatus(id, (String) null);
  }

  public void childExistenceCheckBookRentalStatus(Long id, String messageId) {
    String entityMsgIdPart = "jp.ecuacion.splib.core.entity.bookRentalStatus";
    internalChildExistenceCheck(bookRentalStatusRepo.findByBook_Id(id), messageId, entityMsgIdPart);
  }

  public void childExistenceCheckBookRentalStatus(Long id, ChildExistenceCheckConditionBean... conditions) {
    String entityMsgIdPart = "jp.ecuacion.splib.core.entity.bookRentalStatus";
    internalChildExistenceCheck(bookRentalStatusRepo.findByBook_Id(id), entityMsgIdPart, conditions);
  }

  public void childExistenceCheckBookRentalStatus(BookBaseRecord rec, ChildExistenceCheckConditionBean[] conditions, String referingRecordDataLabel, String recordSpecifyingFieldName) {
    String entityMsgIdPart = "jp.ecuacion.splib.core.entity.bookRentalStatus";
    internalChildExistenceCheck(bookRentalStatusRepo.findByBook_Id(rec.getIdOfEntityDataType()), null, entityMsgIdPart, conditions, referingRecordDataLabel, recordSpecifyingFieldName);
  }

  public void allChildrenExistenceChecks(Long id) {
    allChildrenExistenceChecks(id, null);
  }

  public void allChildrenExistenceChecks(Long id, String messageId, Class<?>... clses) {
    List<Class<?>> skipList = Arrays.asList(clses);

    if (!skipList.contains(BookRentalStatus.class)) childExistenceCheckBookRentalStatus(id, messageId);
  }
}
