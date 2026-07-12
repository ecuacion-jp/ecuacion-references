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
package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.sle.service;

import jakarta.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.ecuacion.referenceapps.splib.web.tutorial.bl.ApplicationScopeDataStoreBl;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.MonthUtil;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record.MonthRecord;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.sle.form.PtSle21PtnListForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.sle.form.PtSle21PtnSearchForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.ServiceCommonUtil;
import jp.ecuacion.lib.core.util.FileUtil;
import jp.ecuacion.splib.core.record.SplibRecord;
import jp.ecuacion.splib.web.service.SplibSearchListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class PtSle21PtnSearchListService
    extends SplibSearchListService<PtSle21PtnSearchForm, PtSle21PtnListForm> {

  private static final String FUNCTION_NAME = "ptSle21Ptn";

  @Autowired
  private ServletContext context;

  @Autowired
  private ServiceCommonUtil serviceUtil;

  @Override
  public void prepareForm(PtSle21PtnSearchForm searchForm, PtSle21PtnListForm listForm,
      UserDetails loginUser) {

  }

  @Override
  public void page(PtSle21PtnSearchForm searchForm, PtSle21PtnListForm listForm,
      UserDetails loginUser) throws IOException {

    // 1.検索条件適用結果の全件数取得と設定

    // ALLデータの取得
    List<MonthRecord> allList = new ArrayList<>();
    ApplicationScopeDataStoreBl storeBl =
        new ApplicationScopeDataStoreBl(context, FUNCTION_NAME, MonthUtil.getInitialData());
    storeBl.getList().stream().forEach(rec -> allList.add((MonthRecord) rec));

    // DBではないので条件フィルタは愚直に実施・・mapのkeyはid。
    Map<String, MonthRecord> mapToRemove = new HashMap<>();
    for (MonthRecord rec : allList) {
      final boolean filterJapanese =
          filter(searchForm.getMonth().getNameJapanese(), rec.getNameJapanese());
      final boolean filterJapaneseOld =
          filter(searchForm.getMonth().getNameJapaneseOld(), rec.getNameJapaneseOld());
      final boolean filterEnglish =
          filter(searchForm.getMonth().getNameEnglish(), rec.getNameEnglish());
      final boolean filterGerman =
          filter(searchForm.getMonth().getNameGerman(), rec.getNameGerman());
      final boolean filterFrench =
          filter(searchForm.getMonth().getNameFrench(), rec.getNameFrench());
      final boolean filterSeason = filter(searchForm.getMonth().getSeason(), rec.getSeason());

      if (filterJapanese || filterJapaneseOld || filterEnglish || filterGerman || filterFrench
          || filterSeason) {
        mapToRemove.put(rec.getId(), rec);
      }
    }

    mapToRemove.values().stream().forEach(rec -> allList.remove(rec));

    // searchFormに全件数設定
    searchForm.setNumberOfRecordsAndAdjustCurrentPageNumger(Long.valueOf(allList.size()));

    // 2.表示件数・ページ番号を踏まえた表示対象レコードデータの取得と設定

    // sort
    List<? extends SplibRecord> sortedList =
        getSortedList(allList, searchForm, new String[] {"id"});
    List<MonthRecord> finalList =
        getFilteredList(sortedList, searchForm).stream().map(rec -> (MonthRecord) rec).toList();

    ((PtSle21PtnListForm) listForm).setRecList(finalList);

    // 3.楽観的排他制御のための情報設定

    // version項目にfileのtimestampを埋める
    for (MonthRecord rec : finalList) {
      rec.setVersion(FileUtil
          .getLockFileVersion(new File(serviceUtil.getLockFilePath(FUNCTION_NAME, rec.getId()))));
    }
  }

  private boolean filter(String valInForm, String valInServer) {
    return valInForm != null && !valInForm.equals("")
        && !valInServer.toUpperCase().contains(valInForm.toUpperCase());
  }

  @Override
  public void delete(PtSle21PtnListForm listForm, UserDetails loginUser) {
    MonthRecord rec = listForm.getMonth();

    ApplicationScopeDataStoreBl storeBl =
        new ApplicationScopeDataStoreBl(context, FUNCTION_NAME, MonthUtil.getInitialData());

    storeBl.deleteRecord(rec.getId());
  }
}
