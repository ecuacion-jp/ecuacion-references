package jp.ecuacion.referenceapps.splib.web.tutorial.repository;

import jp.ecuacion.referenceapps.splib.web.tutorial.base.entity.YourName;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.repository.YourNameBaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface YourNameRepository
    extends YourNameBaseRepository, JpaSpecificationExecutor<YourName> {

}
