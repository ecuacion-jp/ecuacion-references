package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform;

import java.util.Locale;
import jp.ecuacion.lib.core.util.PropertyFileUtil;

/**
 * 
 */
public enum SeasonEnum {

  SPRING("1"), SUMMER("2"), FALL("3"), WINTER("4");

  private String code;

  private SeasonEnum(String code) {
    this.code = code;
  }

  /**
   * codeを返す。 codeがnull, 空文字の場合は、Enum生成時にチェックエラーとなるため考慮不要
   */
  public String getCode() {
    return code;
  }

  /**
   * 画面で表示するための名称を返す。
   * この名称は、getはできるがそれをもとにenumを取得することはできない。
   * localizeされた言語で返す。
   */
  public String getDisplayName(Locale locale) {
    return PropertyFileUtil.getEnumName(locale,
        this.getClass().getSimpleName() + "." + this.toString());
  }

  /**
   * defaultのLocaleを使用。
   */
  public String getDisplayName() {
    return PropertyFileUtil.getEnumName(Locale.getDefault(),
        this.getClass().getSimpleName() + "." + this.toString());
  }
}
