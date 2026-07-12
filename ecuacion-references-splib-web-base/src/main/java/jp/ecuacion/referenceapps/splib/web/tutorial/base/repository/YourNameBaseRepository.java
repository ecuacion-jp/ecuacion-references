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
package jp.ecuacion.referenceapps.splib.web.tutorial.base.repository;

import java.util.*;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.entity.YourName;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface YourNameBaseRepository extends SystemCommonBaseRepository<YourName, Long>, JpaSpecificationExecutor<YourName> {

  @Query(value = "from YourName where id = :id")
  Optional<YourName> findById(Long id);

  Optional<YourName> findByName(String name);

  @Query(nativeQuery = true, value = "select * from Instance where del_flg = false")
  public List<YourName> findAllFromAllGroups();

  @Query(nativeQuery = true,
      value = "select * from YOUR_NAME where ID = :#{#entity.id} and is_deleted = true")
  Optional<YourName> findByIdAndSoftDeleteFieldTrueFromAllGroups(@Param("entity") YourName entity);

  @Query(nativeQuery = true,
      value = "select * from YOUR_NAME where name = :#{#entity.name} and is_deleted = true")
  Optional<YourName> findByNaturalKeyAndSoftDeleteFieldTrueFromAllGroups(@Param("entity") YourName entity);

  @Modifying
  @Query(nativeQuery = true,
      value = "delete from YOUR_NAME where ID = :#{#entity.id} and is_deleted = true")
  void deleteByIdAndSoftDeleteFieldTrueFromAllGroups(@Param("entity") YourName entity);

}
