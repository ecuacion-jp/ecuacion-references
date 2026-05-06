package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ServiceCommonUtil {

  @Autowired
  private Environment env;

  /** データレコード単位でのlockを行う。idはレコードのkey。 */
  public String getLockFilePath(String function, String id) {
    return env.getProperty("app.work-dir") + "/" + function + "/lock/" + id + ".lock";
  }
}
