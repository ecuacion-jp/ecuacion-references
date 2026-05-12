package jp.ecuacion.referenceapps.splib.web.tutorial.base.bl;

import java.util.List;
import java.util.Optional;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.entity.*;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.record.*;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.repository.*;
import jp.ecuacion.splib.jpa.repository.SplibRepository;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class YourNameBaseBl extends SystemCommonBaseBl<YourName, Long> {

  @Autowired
  protected YourNameBaseRepository repo;

  @Override
  public SplibRepository<YourName, Long> getRepositoryForOptimisticLocking() {
    return repo;
  }

  public YourName findAndOptimisticLockingCheck(YourNameBaseRecord rec) {
    return findAndOptimisticLockingCheck(rec.getIdOfEntityDataType(), rec.getVersionOfEntityDataType());
  }

  public YourName insertOrUpdate(YourNameBaseRecord rec, String... skipUpdateFields) {
    YourName e = null;
    boolean isInsert = rec.getId() == null || rec.getId().equals("");

    if (isInsert) {
      e = new YourName(rec);

    } else {
      e = findAndOptimisticLockingCheck(rec);
      e.update(rec, skipUpdateFields);
    }

    return repo.save(e);
  }

  private void duplicateCheck(boolean isCheckFromAllGroups, List<YourName> entityList, YourNameBaseRecord rec, String... targetItemPropertyPaths) {
    internalDuplicateCheck(isCheckFromAllGroups, entityList, rec, "yourName", "id", targetItemPropertyPaths);
  }

  public void duplicateCheck(List<YourName> entityList, YourNameBaseRecord rec, String... targetItemPropertyPaths) {
    duplicateCheck(false, entityList, rec, targetItemPropertyPaths);
  }

  public void duplicateCheck(YourNameBaseRecord rec, String... targetItemPropertyPaths) {
    duplicateCheck(repo.findAll(), rec, targetItemPropertyPaths);
  }

  public void duplicateCheckFromAllGroups(List<YourName> entityList, YourNameBaseRecord rec, String... targetItemPropertyPaths) {
    duplicateCheck(true, entityList, rec, targetItemPropertyPaths);
  }

  public void duplicateCheckFromAllGroups(YourNameBaseRecord rec, String... targetItemPropertyPaths) {
    duplicateCheckFromAllGroups(repo.findAllFromAllGroups(), rec, targetItemPropertyPaths);
  }

  public void naturalKeyDuplicateCheck(YourNameBaseRecord rec) {
    Optional<YourName> optional = repo.findByName(rec.getName());
    throwExceptionWhenDuplicated(optional.isPresent() && !optional.get().getId().equals(rec.getIdOfEntityDataType()), false, new String[] {"name"}, new String[] {rec.getItem("name").getItemNameKey()});
  }

}
