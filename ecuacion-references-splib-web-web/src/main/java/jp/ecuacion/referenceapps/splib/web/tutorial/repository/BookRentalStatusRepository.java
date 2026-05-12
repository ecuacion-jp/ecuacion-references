package jp.ecuacion.referenceapps.splib.web.tutorial.repository;

import jp.ecuacion.referenceapps.splib.web.tutorial.base.entity.BookRentalStatus;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.repository.BookRentalStatusBaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRentalStatusRepository
    extends BookRentalStatusBaseRepository, JpaSpecificationExecutor<BookRentalStatus> {

}
