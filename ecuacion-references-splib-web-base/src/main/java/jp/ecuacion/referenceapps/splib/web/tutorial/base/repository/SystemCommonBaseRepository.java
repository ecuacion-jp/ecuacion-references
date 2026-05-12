package jp.ecuacion.referenceapps.splib.web.tutorial.base.repository;

import jp.ecuacion.splib.jpa.repository.SplibRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SystemCommonBaseRepository<T, I> extends SplibRepository<T, I> {

}
