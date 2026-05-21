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
