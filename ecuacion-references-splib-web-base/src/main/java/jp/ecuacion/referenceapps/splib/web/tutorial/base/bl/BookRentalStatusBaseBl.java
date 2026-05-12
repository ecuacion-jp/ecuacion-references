package jp.ecuacion.referenceapps.splib.web.tutorial.base.bl;

import java.util.List;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.entity.*;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.record.*;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.repository.*;
import jp.ecuacion.splib.jpa.repository.SplibRepository;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BookRentalStatusBaseBl extends SystemCommonBaseBl<BookRentalStatus, Long> {

  @Autowired
  protected BookRentalStatusBaseRepository repo;

  @Override
  public SplibRepository<BookRentalStatus, Long> getRepositoryForOptimisticLocking() {
    return repo;
  }

  public BookRentalStatus findAndOptimisticLockingCheck(BookRentalStatusBaseRecord rec) {
    return findAndOptimisticLockingCheck(rec.getBookIdOfEntityDataType(), rec.getVersionOfEntityDataType());
  }

  public BookRentalStatus insertOrUpdate(BookRentalStatusBaseRecord rec, Book book, String... skipUpdateFields) {
    BookRentalStatus e = null;
    boolean isInsert = rec.getBookId() == null || rec.getBookId().equals("");

    if (isInsert) {
      e = new BookRentalStatus(rec, book);

    } else {
      e = findAndOptimisticLockingCheck(rec);
      e.update(rec, book, skipUpdateFields);
    }

    return repo.save(e);
  }

  private void duplicateCheck(boolean isCheckFromAllGroups, List<BookRentalStatus> entityList, BookRentalStatusBaseRecord rec, String... targetItemPropertyPaths) {
    internalDuplicateCheck(isCheckFromAllGroups, entityList, rec, "bookRentalStatus", "id", targetItemPropertyPaths);
  }

  public void duplicateCheck(List<BookRentalStatus> entityList, BookRentalStatusBaseRecord rec, String... targetItemPropertyPaths) {
    duplicateCheck(false, entityList, rec, targetItemPropertyPaths);
  }

  public void duplicateCheck(BookRentalStatusBaseRecord rec, String... targetItemPropertyPaths) {
    duplicateCheck(repo.findAll(), rec, targetItemPropertyPaths);
  }

  public void duplicateCheckFromAllGroups(List<BookRentalStatus> entityList, BookRentalStatusBaseRecord rec, String... targetItemPropertyPaths) {
    duplicateCheck(true, entityList, rec, targetItemPropertyPaths);
  }

  public void duplicateCheckFromAllGroups(BookRentalStatusBaseRecord rec, String... targetItemPropertyPaths) {
    duplicateCheckFromAllGroups(repo.findAllFromAllGroups(), rec, targetItemPropertyPaths);
  }

}
