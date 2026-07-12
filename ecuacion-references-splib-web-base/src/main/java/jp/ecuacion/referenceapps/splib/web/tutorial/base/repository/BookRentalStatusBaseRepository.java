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
import jp.ecuacion.referenceapps.splib.web.tutorial.base.entity.BookRentalStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface BookRentalStatusBaseRepository extends SystemCommonBaseRepository<BookRentalStatus, Long>, JpaSpecificationExecutor<BookRentalStatus> {

  @Query(value = "from BookRentalStatus where bookId = :id")
  Optional<BookRentalStatus> findById(Long id);

  public Optional<BookRentalStatus> findByBook_Id(Long id);

  @Query(nativeQuery = true, value = "select * from Instance where del_flg = false")
  public List<BookRentalStatus> findAllFromAllGroups();

  @Query(nativeQuery = true,
      value = "select * from BOOK_RENTAL_STATUS where BOOK_ID = :#{#entity.bookId} and is_deleted = true")
  Optional<BookRentalStatus> findByIdAndSoftDeleteFieldTrueFromAllGroups(@Param("entity") BookRentalStatus entity);

  @Query(nativeQuery = true,
      value = "select * from BOOK_RENTAL_STATUS where 1 = 2 and is_deleted = true")
  Optional<BookRentalStatus> findByNaturalKeyAndSoftDeleteFieldTrueFromAllGroups(@Param("entity") BookRentalStatus entity);

  @Modifying
  @Query(nativeQuery = true,
      value = "delete from BOOK_RENTAL_STATUS where BOOK_ID = :#{#entity.bookId} and is_deleted = true")
  void deleteByIdAndSoftDeleteFieldTrueFromAllGroups(@Param("entity") BookRentalStatus entity);

}
