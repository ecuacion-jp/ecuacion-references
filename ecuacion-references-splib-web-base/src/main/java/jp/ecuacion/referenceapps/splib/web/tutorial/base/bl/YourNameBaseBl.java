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
