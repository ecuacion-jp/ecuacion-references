package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform;

import jp.ecuacion.lib.core.util.PropertiesFileUtil;
import org.springframework.stereotype.Component;

@Component
public class ServiceCommonUtil {

  /** データレコード単位でのlockを行う。idはレコードのkey。 */
  public String getLockFilePath(String function, String id) {
    return PropertiesFileUtil.getApplication("app.work-dir") + "/" + function + "/lock/" + id
        + ".lock";
  }
}
