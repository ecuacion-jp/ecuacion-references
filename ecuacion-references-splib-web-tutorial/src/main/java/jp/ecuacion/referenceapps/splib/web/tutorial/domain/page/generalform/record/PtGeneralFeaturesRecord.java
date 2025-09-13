package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jp.ecuacion.splib.core.form.record.SplibRecord;
import jp.ecuacion.splib.web.bean.HtmlItem;
import jp.ecuacion.splib.web.bean.HtmlItemString;
import jp.ecuacion.splib.web.form.record.RecordInterface;

public class PtGeneralFeaturesRecord extends SplibRecord implements RecordInterface {
  private String version;

  @DecimalMin(value = "100")
  @DecimalMax(value = "200")
  private String validationTest;

  static {
    getStringLengthMap().put("validationTest", 4);
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getValidationTest() {
    return validationTest;
  }


  public void setValidationTest(String validationTest) {
    this.validationTest = validationTest;
  }

  @Override
  public HtmlItem[] getHtmlItems() {
    return new HtmlItem[] {new HtmlItemString("validationTest").notEmpty()};
  }
}
