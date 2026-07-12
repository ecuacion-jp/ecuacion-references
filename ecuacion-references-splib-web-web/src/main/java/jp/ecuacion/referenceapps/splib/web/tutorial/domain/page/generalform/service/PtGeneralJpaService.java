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
