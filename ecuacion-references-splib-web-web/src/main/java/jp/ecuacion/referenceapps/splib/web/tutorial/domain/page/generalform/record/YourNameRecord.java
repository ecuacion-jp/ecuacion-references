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

import jp.ecuacion.referenceapps.splib.web.tutorial.base.entity.YourName;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.record.YourNameBaseRecord;
import jp.ecuacion.splib.core.container.DatetimeFormatParameters;
import jp.ecuacion.splib.web.item.HtmlItem;
import jp.ecuacion.splib.web.item.HtmlItemString;
import jp.ecuacion.splib.web.item.HtmlItemContainer;

public class YourNameRecord extends YourNameBaseRecord implements HtmlItemContainer {

  public YourNameRecord() {
    super();
  }

  public YourNameRecord(YourName e, DatetimeFormatParameters params) {
    super(e, params);
  }

  @Override
  public HtmlItem[] customizedItems() {
    return new HtmlItem[] {new HtmlItemString("validationTest").notEmpty()};
  }
}
