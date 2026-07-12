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

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import jp.ecuacion.lib.core.util.EnumUtil;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.SeasonEnum;
import jp.ecuacion.splib.web.item.HtmlItem;
import jp.ecuacion.splib.web.item.HtmlItemString;
import jp.ecuacion.splib.web.item.HtmlItemContainer;

public class MonthRecord extends RecordWithId
    implements HtmlItemContainer {

  // 月を示す1-12の数字をidとして保持

  private String nameJapanese;
  private String nameJapaneseOld;
  private String nameEnglish;
  private String nameGerman;
  private String nameFrench;
  private String season;
  private String numberOfDays;
  private String version;

  static final HtmlItem[] htmlItems =
      new HtmlItem[] {
          new HtmlItem("id"),
          new HtmlItemString("nameJapanese"),
          new HtmlItemString("nameJapaneseOld"),
          new HtmlItemString("nameEnglish"),
          new HtmlItemString("nameGerman"),
          new HtmlItemString("nameFrench"),
          new HtmlItem("season"),
          new HtmlItem("numberOfDays")
          };

  static {
    getStringLengthMap().put("id", 30);
    getStringLengthMap().put("nameJapanese", 30);
    getStringLengthMap().put("nameJapaneseOld", 30);
    getStringLengthMap().put("nameEnglish", 30);
    getStringLengthMap().put("nameGerman", 30);
    getStringLengthMap().put("nameFrench", 30);
    getStringLengthMap().put("numberOfDays", 2);
  }
  
  public MonthRecord() {
    super();
  }

  public MonthRecord(String id, String nameJapanese, String nameJapaneseOld, String nameEnglish,
      String nameGerman, String nameFrench, SeasonEnum season, int numberOfDays) {
    super();

    this.id = id;
    this.nameJapanese = nameJapanese;
    this.nameJapaneseOld = nameJapaneseOld;
    this.nameEnglish = nameEnglish;
    this.nameGerman = nameGerman;
    this.nameFrench = nameFrench;
    this.season = season.getCode();
    this.numberOfDays = Integer.toString(numberOfDays);
    this.version = "1";
  }

  @Override
  public HtmlItem[] customizedItems() {
    return htmlItems;
  }
  
  public String getNameJapanese() {
    return nameJapanese;
  }

  public void setNameJapanese(String nameJapanese) {
    this.nameJapanese = nameJapanese;
  }

  public String getNameJapaneseOld() {
    return nameJapaneseOld;
  }

  public void setNameJapaneseOld(String nameJapaneseOld) {
    this.nameJapaneseOld = nameJapaneseOld;
  }

  public String getNameEnglish() {
    return nameEnglish;
  }

  public void setNameEnglish(String nameEnglish) {
    this.nameEnglish = nameEnglish;
  }

  public String getNameGerman() {
    return nameGerman;
  }

  public void setNameGerman(String nameGerman) {
    this.nameGerman = nameGerman;
  }

  public String getNameFrench() {
    return nameFrench;
  }

  public void setNameFrench(String nameFrench) {
    this.nameFrench = nameFrench;
  }

  public String getSeason() {
    return season;
  }

  public void setSeason(String season) {
    this.season = season;
  }

  public String getNumberOfDays() {
    return numberOfDays;
  }

  public void setNumberOfDays(String numberOfDays) {
    this.numberOfDays = numberOfDays;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public List<String[]> getSeasonList(Locale locale, String options) {
    return EnumUtil.getListForHtmlSelect(SeasonEnum.class, locale, options);
  }

  public String getSeasonName(Locale locale) {
    SeasonEnum anEnum = EnumUtil.getEnumFromCode(SeasonEnum.class, season);
    
    return Objects.requireNonNull(anEnum).getDisplayName(locale);
  }
}
