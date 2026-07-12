/*
 * Copyright © 2012 ecuacion.jp (info@ecuacion.jp)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jp.ecuacion.splib.core.record.SplibRecord;
import jp.ecuacion.splib.web.item.HtmlItem;
import jp.ecuacion.splib.web.item.HtmlItemString;
import jp.ecuacion.splib.web.item.HtmlItemContainer;

public class PtGeneralFeaturesRecord extends SplibRecord implements HtmlItemContainer {
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
  public HtmlItem[] customizedItems() {
    return new HtmlItem[] {new HtmlItemString("validationTest").notEmpty()};
  }
}
