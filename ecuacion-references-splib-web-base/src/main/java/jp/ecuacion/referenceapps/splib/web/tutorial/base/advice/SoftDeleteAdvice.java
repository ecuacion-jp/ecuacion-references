package jp.ecuacion.referenceapps.splib.web.tutorial.base.advice;

import jp.ecuacion.splib.jpa.advice.SplibSoftDeleteAdvice;
import jp.ecuacion.splib.jpa.util.SplibJpaFilterUtil;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SoftDeleteAdvice extends SplibSoftDeleteAdvice {

  public SoftDeleteAdvice(SplibJpaFilterUtil splibJpaFilterUtil) {
    super(splibJpaFilterUtil);
  }
}
