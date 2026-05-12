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
