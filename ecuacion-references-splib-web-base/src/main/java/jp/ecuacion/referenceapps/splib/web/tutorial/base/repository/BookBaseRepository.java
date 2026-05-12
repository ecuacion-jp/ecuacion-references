package jp.ecuacion.referenceapps.splib.web.tutorial.base.repository;

import java.util.*;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.entity.Book;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface BookBaseRepository extends SystemCommonBaseRepository<Book, Long>, JpaSpecificationExecutor<Book> {

  @Query(value = "from Book where id = :id")
  Optional<Book> findById(Long id);

  @Query(nativeQuery = true, value = "select * from Instance where del_flg = false")
  public List<Book> findAllFromAllGroups();

  @Query(nativeQuery = true,
      value = "select * from BOOK where ID = :#{#entity.id} and is_deleted = true")
  Optional<Book> findByIdAndSoftDeleteFieldTrueFromAllGroups(@Param("entity") Book entity);

  @Query(nativeQuery = true,
      value = "select * from BOOK where 1 = 2 and is_deleted = true")
  Optional<Book> findByNaturalKeyAndSoftDeleteFieldTrueFromAllGroups(@Param("entity") Book entity);

  @Modifying
  @Query(nativeQuery = true,
      value = "delete from BOOK where ID = :#{#entity.id} and is_deleted = true")
  void deleteByIdAndSoftDeleteFieldTrueFromAllGroups(@Param("entity") Book entity);

}
