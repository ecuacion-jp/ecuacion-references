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
package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform;

import java.util.Locale;
import jp.ecuacion.lib.core.util.PropertiesFileUtil;

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
    return PropertiesFileUtil.getEnumName(locale,
        this.getClass().getSimpleName() + "." + this.toString());
  }

  /**
   * defaultのLocaleを使用。
   */
  public String getDisplayName() {
    return PropertiesFileUtil.getEnumName(Locale.getDefault(),
        this.getClass().getSimpleName() + "." + this.toString());
  }
}
