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
package jp.ecuacion.referenceapps.splib.web.tutorial.bl;

import jakarta.servlet.ServletContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record.RecordWithId;
import jp.ecuacion.splib.core.record.SplibRecord;

/**
 * サンプルデータ格納のために、メモリ上のapplicationスコープの空間を使用する。
 *
 * <p>
 * sessionだと排他制御などの確認がしにくくイマイチなので。ただ、再起動するまでデータが保持されてしまうのはサンプルとしてはそれはそれでイマイチなので、
 * コンストラクタに初期データを渡した場合は値が初期化されるものとする。
 * </p>
 *
 * <p>
 * 値の保持は、"data"というkeyをトップにおき、その値はMap&lt;String, List&lt;SplibRecord&gt;&gt; とする。
 * Mapのkeyはfunctionで、それに対してrecordのlistが存在する、という形。
 * </p>
 */
public class ApplicationScopeDataStoreBl {

  private ServletContext context;
  private String function;

  private static final String ROOT_KEY = "data";

  /**
   * 初期値は設定済みの前提で、データ操作を行うためにconstructorを呼び出す場合はこれを使用。 初期値が未設定の場合はエラーとする。
   */
  public ApplicationScopeDataStoreBl(ServletContext context, String function) {
    this.context = context;
    this.function = function;

    createRootMap();

    if (!getRootMap().containsKey(function)) {
      throw new RuntimeException("サーバ起動後初回の呼び出しは、initialDataを含んだconstructorを呼び出す必要があります。");
    }
  }

  /** 初期値が設定されていない場合は設定する場合はこちらを使用。 */
  public ApplicationScopeDataStoreBl(ServletContext context, String function,
      List<? extends RecordWithId> initialData) {
    this.context = context;
    this.function = function;

    createRootMap();

    if (!getRootMap().containsKey(function)) {
      getRootMap().put(function, initialData);
    }
  }

  private void createRootMap() {
    if (context.getAttribute(ROOT_KEY) == null) {
      context.setAttribute(ROOT_KEY, new HashMap<String, List<SplibRecord>>());
    }
  }

  @SuppressWarnings("unchecked")
  private Map<String, List<? extends RecordWithId>> getRootMap() {
    return (Map<String, List<? extends RecordWithId>>) context.getAttribute(ROOT_KEY);
  }

  public List<RecordWithId> getList() {
    return new ArrayList<>(getRootMap().get(function));
  }

  public RecordWithId getRecord(String id) {
    List<RecordWithId> list = getList().stream().filter(rec -> rec.getId().equals(id)).toList();
    return list.size() == 0 ? null : list.get(0);
  }

  /** insert. */
  public void insertRecord(RecordWithId record) {
    List<RecordWithId> list = getList();
    // id重複チェック
    if (list.stream().filter(rec -> rec.getId().equals(record.getId())).toList().size() > 0) {
      throw new RuntimeException("Keyが重複しています。");
    }

    list.add(record);
    getRootMap().put(function, list);
  }

  /** update. */
  public void updateRecord(RecordWithId record) {
    List<RecordWithId> list = new ArrayList<>(
        getList().stream().filter(rec -> !rec.getId().equals(record.getId())).toList());
    list.add(record);
    getRootMap().put(function, list);
  }

  /** delete. */
  public void deleteRecord(String id) {
    List<RecordWithId> list = getList().stream().filter(rec -> !rec.getId().equals(id)).toList();
    getRootMap().put(function, list);
  }
}
