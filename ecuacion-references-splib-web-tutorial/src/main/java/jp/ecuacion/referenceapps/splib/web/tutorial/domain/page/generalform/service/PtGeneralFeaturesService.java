package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import jp.ecuacion.lib.core.util.FileUtil;
import jp.ecuacion.lib.core.violation.Violations;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.ServiceCommonUtil;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneralFeaturesForm;
import jp.ecuacion.splib.web.service.SplibGeneralService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PtGeneralFeaturesService extends SplibGeneralService {

  @Autowired
  private ServiceCommonUtil serviceUtil;

  public void page(PtGeneralFeaturesForm form, String function) throws IOException {

    File lockFile = new File(serviceUtil.getLockFilePath(function, function));

    form.getPtGeneralFeatures().setVersion(FileUtil.getLockFileVersion(lockFile));
  }

  /** ファイルロックを取得し、5秒待機後正常終了。 */
  public void exclusiveControlFile(PtGeneralFeaturesForm form, String function) throws Exception {

    File lockFile = new File(serviceUtil.getLockFilePath(function, function));

    Pair<FileChannel, FileLock> lockedObject =
        FileUtil.lock(lockFile, form.getPtGeneralFeatures().getVersion());

    Thread.sleep(5000);

    FileUtil.release(lockedObject);
  }

  /** Throws warning. */
  public void warning(PtGeneralFeaturesForm form) {
    // warning1回目
    if (!form.getConfirmedWarningMessageSet().contains("PT_GENERAL_FEATURES_MSG_WARNING_1")) {
      new Violations().add("PT_GENERAL_FEATURES_MSG_WARNING_1").throwWarningIfAny();
    }

    // warning2回目
    if (!form.getConfirmedWarningMessageSet().contains("PT_GENERAL_FEATURES_MSG_WARNING_2")) {
      new Violations().add("PT_GENERAL_FEATURES_MSG_WARNING_2").throwWarningIfAny();
    }
  }
}
