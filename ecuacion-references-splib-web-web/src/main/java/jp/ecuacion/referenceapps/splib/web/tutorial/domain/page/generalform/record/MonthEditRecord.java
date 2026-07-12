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

import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.SeasonEnum;
import jp.ecuacion.splib.web.item.HtmlItem;
import jp.ecuacion.splib.web.item.HtmlItemContainer;
import jp.ecuacion.splib.web.item.HtmlItemString;

public class MonthEditRecord extends MonthRecord implements HtmlItemContainer {

  static final HtmlItem[] htmlItems =
      new HtmlItem[] {
          new HtmlItem("id").notEmpty(),
          new HtmlItemString("nameJapanese").notEmpty(),
          new HtmlItemString("nameJapaneseOld").notEmpty(),
          new HtmlItemString("nameEnglish").notEmpty(),
          new HtmlItemString("nameGerman").notEmpty(),
          new HtmlItemString("nameFrench").notEmpty(),
          new HtmlItem("season").notEmpty(),
          new HtmlItem("numberOfDays").notEmpty()
          };

  public MonthEditRecord() {
    super();
  }

  public MonthEditRecord(String id, String nameJapanese, String nameJapaneseOld,
      String nameEnglish, String nameGerman, String nameFrench, SeasonEnum season,
      int numberOfDays) {
    super(id, nameJapanese, nameJapaneseOld, nameEnglish, nameGerman, nameFrench, season,
        numberOfDays);
  }

  /** MonthRecord からフィールドをコピーして MonthEditRecord を生成する。 */
  public MonthEditRecord(MonthRecord src) {
    super();
    this.setId(src.getId());
    this.setNameJapanese(src.getNameJapanese());
    this.setNameJapaneseOld(src.getNameJapaneseOld());
    this.setNameEnglish(src.getNameEnglish());
    this.setNameGerman(src.getNameGerman());
    this.setNameFrench(src.getNameFrench());
    this.setSeason(src.getSeason());
    this.setNumberOfDays(src.getNumberOfDays());
    this.setVersion(src.getVersion());
  }

  @Override
  public HtmlItem[] customizedItems() {
    return htmlItems;
  }
}
