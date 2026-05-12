package jp.ecuacion.referenceapps.splib.web.tutorial.base.bl;

import jp.ecuacion.referenceapps.splib.web.tutorial.base.entity.*;
import jp.ecuacion.splib.jpa.bl.*;

public abstract class SystemCommonBaseBl<E extends SystemCommon, I> extends SplibJpaBl<E, I, Long> {

  @Override
  public Long getVersionForOptimisticLocking(SystemCommon e) {
    return e.getVersion();
  }

}
