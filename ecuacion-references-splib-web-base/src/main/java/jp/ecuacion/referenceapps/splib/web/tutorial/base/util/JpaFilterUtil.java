package jp.ecuacion.referenceapps.splib.web.tutorial.base.util;

import jp.ecuacion.splib.jpa.util.SplibJpaFilterUtil;
import org.springframework.stereotype.Component;

@Component
public class JpaFilterUtil extends SplibJpaFilterUtil {

  public JpaFilterUtil() {
    super(true, false, null, false, null, null);
  }
}
