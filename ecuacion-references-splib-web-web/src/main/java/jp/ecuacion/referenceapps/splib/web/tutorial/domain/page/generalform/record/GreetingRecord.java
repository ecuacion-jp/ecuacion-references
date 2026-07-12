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

import jp.ecuacion.splib.core.record.SplibRecord;
import jp.ecuacion.splib.web.item.HtmlItem;
import jp.ecuacion.splib.web.item.HtmlItemContainer;

public class GreetingRecord extends SplibRecord implements HtmlItemContainer {

  // private String serverTimestamp;

  private String name;
  private String greetingMessage;

  // public String getServerTimestamp() {
  // return serverTimestamp == null || serverTimestamp == "" ? "（未取得）" : serverTimestamp;
  // }
  //
  // public void setServerTimestamp(String serverTimestamp) {
  // this.serverTimestamp = serverTimestamp;
  // }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getGreetingMessage() {
    return greetingMessage;
  }

  public void setGreetingMessage(String greetingMessage) {
    this.greetingMessage = greetingMessage;
  }

  @Override
  public HtmlItem[] customizedItems() {
    return new HtmlItem[] {new HtmlItem("name").notEmpty()};
  }
}
