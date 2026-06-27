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
package jp.ecuacion.referenceapps.util.util;

import jakarta.servlet.ServletContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.ecuacion.lib.core.violation.BusinessViolation;
import jp.ecuacion.lib.core.violation.Violations;
import jp.ecuacion.referenceapps.util.record.RecordWithId;
import jp.ecuacion.splib.core.record.SplibRecord;
import org.jspecify.annotations.Nullable;

/**
 * Provides a data store access. The store keeps lists of objects 
 *     on {@code Application Scope} of the memory space.
 * 
 * <p>It's just a simple data store, 
 *     so transaction or exclusive controls are not implemented.</p>
 * 
 * <p>Lists are stored in a {@code Map<String, List<? extends RecordWithId>>}.
 *     String key {@code dataKind} expresses the kind of the data, 
 *     so you can think of it as a table name of relational databases.</p>
 */
public class MemoryDataStoreDao {
  private ServletContext context;
  private String dataKind;

  private static final String ROOT_KEY = "data";

  /**
   * 初期値は設定済みの前提で、データ操作を行うためにconstructorを呼び出す場合はこれを使用。 初期値が未設定の場合はエラーとする。
   */
  public MemoryDataStoreDao(ServletContext context, String dataKind) {
    this(context, dataKind, null);
  }

  /** 初期値が設定されていない場合は設定する場合はこちらを使用。 */
  public MemoryDataStoreDao(ServletContext context, String function,
      List<? extends RecordWithId> initialData) {
    this.context = context;
    this.dataKind = function;

    initinalize(dataKind, initialData);
  }

  @SuppressWarnings("unchecked")
  private Map<String, List<? extends RecordWithId>> getRootMap() {
    return (Map<String, List<? extends RecordWithId>>) context.getAttribute(ROOT_KEY);
  }

  private void initinalize(String dataKind, @Nullable List<? extends RecordWithId> dataList) {
    // create rootMap if it doesn't exist
    if (context.getAttribute(ROOT_KEY) == null) {
      context.setAttribute(ROOT_KEY, new HashMap<String, List<SplibRecord>>());
    }

    // create List of dataKind
    if (getRootMap().get(dataKind) == null) {
      getRootMap().put(dataKind, new ArrayList<>());
    }

    // initialize data
    if (dataList != null) {
      getRootMap().put(dataKind, dataList);
    }
  }

  public List<RecordWithId> findAll() {
    return new ArrayList<>(getRootMap().get(dataKind));
  }

  public RecordWithId findById(String id) {
    List<RecordWithId> list = findAll().stream().filter(rec -> rec.getId().equals(id)).toList();
    return list.size() == 0 ? null : list.get(0);
  }

  /** insert. */
  public void insert(RecordWithId record) {
    List<RecordWithId> list = findAll();
    if (list.stream().filter(rec -> rec.getId().equals(record.getId())).toList().size() > 0) {
      throw new RuntimeException("Record with specified ID duplicated.");
    }

    list.add(record);
    getRootMap().put(dataKind, list);
  }

  /** update. */
  public void update(RecordWithId record) {
    if (findAll().stream().filter(rec -> rec.getId().equals(record.getId())).toList().size() == 0) {
      throw new RuntimeException("Record with specified ID not found.");
    }

    List<RecordWithId> list = new ArrayList<>(
        findAll().stream().filter(rec -> !rec.getId().equals(record.getId())).toList());
    list.add(record);
    getRootMap().put(dataKind, list);
  }

  public void insertOrUpdate(RecordWithId record) {
    if (findAll().stream().filter(rec -> rec.getId().equals(record.getId())).toList().size() > 0) {
      update(record);

    } else {
      insert(record);
    }
  }

  /** delete. */
  public void delete(String id) {
    if (findAll().stream().filter(rec -> rec.getId().equals(id)).toList().size() == 0) {
      new Violations().add(new BusinessViolation("Record with specified ID not found."))
          .throwIfAny();
    }

    List<RecordWithId> list = findAll().stream().filter(rec -> !rec.getId().equals(id)).toList();
    getRootMap().put(dataKind, list);
  }

  /** Deletes by id if exists. */
  public void deleteIfExists(String id) {
    if (findAll().stream().filter(rec -> rec.getId().equals(id)).toList().size() > 0) {
      delete(id);
    }
  }
}
