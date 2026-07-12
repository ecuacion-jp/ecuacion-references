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
package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.sle.form;

import org.jspecify.annotations.NonNull;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record.MonthRecord;
import jp.ecuacion.splib.web.form.SplibSearchForm;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class PtSle21PtnSearchForm extends SplibSearchForm {

  private MonthRecord month = new MonthRecord();

  @NonNull
  protected String getDefaultSortItem() {
    return "id";
  }

  public MonthRecord getMonth() {
    return month;
  }

  public void setMonth(MonthRecord month) {
    this.month = month;
  }
}
