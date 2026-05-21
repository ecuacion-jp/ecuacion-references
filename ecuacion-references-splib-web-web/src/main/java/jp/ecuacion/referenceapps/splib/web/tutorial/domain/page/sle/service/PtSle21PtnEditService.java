package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.sle.service;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jp.ecuacion.referenceapps.splib.web.tutorial.bl.ApplicationScopeDataStoreBl;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record.MonthEditRecord;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record.MonthRecord;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record.RecordWithId;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.sle.form.PtSle21PtnEditForm;
import jp.ecuacion.lib.core.violation.BusinessViolation;
import jp.ecuacion.lib.core.violation.Violations;
import jp.ecuacion.splib.web.service.SplibEditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class PtSle21PtnEditService extends SplibEditService<PtSle21PtnEditForm> {

  private static final String function = "ptSle21Ptn";

  @Autowired
  HttpServletRequest request;

  @Autowired
  private ServletContext context;

  public PtSle21PtnEditService() {}

  @Override
  public void prepareForm(PtSle21PtnEditForm form, UserDetails loginUser) {}

  @Override
  public void getInsertPage(PtSle21PtnEditForm form, UserDetails loginUser) {
  }

  @Override
  public void getUpdatePage(PtSle21PtnEditForm form, UserDetails loginUser) {
    String id = form.getMonth().getId();
    String version = form.getMonth().getVersion();
    ApplicationScopeDataStoreBl storeBl = new ApplicationScopeDataStoreBl(context, function);

    MonthRecord rec = (MonthRecord) storeBl.getRecord(id);
    form.setMonth(new MonthEditRecord(rec));

    // 排他制御チェック
    if (!version.equals(rec.getVersion())) {
      throw new ObjectOptimisticLockingFailureException("some class", id);
    }
  }

  @Override
  public void edit(PtSle21PtnEditForm form, UserDetails loginUser) {
    MonthEditRecord rec = form.getMonth();

    // checks
    editCheck(rec, form.isInsert());

    ApplicationScopeDataStoreBl storeBl = new ApplicationScopeDataStoreBl(context, function);
    MonthRecord tmpRec = null;
    if (form.isInsert()) {
      storeBl.insertRecord(rec);

    } else {
      tmpRec = (MonthRecord) storeBl.getRecord(rec.getId());

      tmpRec.setNameJapanese(rec.getNameJapanese());
      tmpRec.setNameJapaneseOld(rec.getNameJapaneseOld());
      tmpRec.setNameEnglish(rec.getNameEnglish());
      tmpRec.setNameGerman(rec.getNameGerman());
      tmpRec.setNameFrench(rec.getNameFrench());
      tmpRec.setSeason(rec.getSeason());
      tmpRec.setNumberOfDays(rec.getNumberOfDays());

      storeBl.updateRecord(tmpRec);
    }
  }

  private void editCheck(MonthEditRecord rec, Boolean isInsert) {
    ApplicationScopeDataStoreBl storeBl = new ApplicationScopeDataStoreBl(context, function);

    if (isInsert) {
      RecordWithId record = storeBl.getRecord(rec.getId());
      if (record != null) {
        new Violations().add(
            new BusinessViolation("PT_SLE_21_PTN_EDIT_MSG_ID_DUPLICATED")).throwIfAny();
      }
    }
  }
}
