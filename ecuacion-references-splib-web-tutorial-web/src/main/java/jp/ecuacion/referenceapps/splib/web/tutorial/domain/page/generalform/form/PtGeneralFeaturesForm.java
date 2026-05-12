package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form;

import jakarta.validation.Valid;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record.PtGeneralFeaturesRecord;
import jp.ecuacion.splib.web.form.SplibGeneralForm;

public class PtGeneralFeaturesForm extends SplibGeneralForm {

  @Valid
  private PtGeneralFeaturesRecord ptGeneralFeatures =
      new PtGeneralFeaturesRecord();

  public PtGeneralFeaturesRecord getPtGeneralFeatures() {
    return ptGeneralFeatures;
  }

  public void setPtGeneralFeatures(PtGeneralFeaturesRecord ptGeneralFeatures) {
    this.ptGeneralFeatures = ptGeneralFeatures;
  }

}
