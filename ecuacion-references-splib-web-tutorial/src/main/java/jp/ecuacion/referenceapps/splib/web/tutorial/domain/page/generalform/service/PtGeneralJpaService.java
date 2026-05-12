package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service;

import java.util.List;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.entity.YourName;
import jp.ecuacion.referenceapps.splib.web.tutorial.bl.YourNameBl;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneralJpaForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record.YourNameRecord;
import jp.ecuacion.referenceapps.splib.web.tutorial.repository.YourNameRepository;
import jp.ecuacion.lib.core.violation.BusinessViolation;
import jp.ecuacion.lib.core.violation.Violations;
import jp.ecuacion.splib.web.jpa.service.SplibGeneralJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PtGeneralJpaService extends SplibGeneralJpaService<YourName> {

  @Autowired
  YourNameRepository repo;

  @Autowired
  YourNameBl bl;

  public void page(PtGeneralJpaForm form, String function) {
    List<YourName> list = repo.findAll();

    if (list.size() == 0) {
      form.setYourName(new YourNameRecord(new YourName(), getParams()));

    } else {
      form.setYourName(new YourNameRecord(list.get(0), getParams()));
    }
  }

  public void action(PtGeneralJpaForm form) throws Exception {
    YourNameRecord rec = form.getYourName();

    if (rec.getName() == null || rec.getName().equals("")) {
      new Violations().add(
          new BusinessViolation("PT_GENERAL_JPA_YOUR_NAME_MSG_NAME_REQUIRED")).throwIfAny();
    }

    List<YourName> list = repo.findAll();

    if (list.size() == 0) {
      repo.save(new YourName(rec));

    } else {
      YourName e = bl.findAndOptimisticLockingCheck(rec);
      e.setName(rec.getName());
    }
  }
}
