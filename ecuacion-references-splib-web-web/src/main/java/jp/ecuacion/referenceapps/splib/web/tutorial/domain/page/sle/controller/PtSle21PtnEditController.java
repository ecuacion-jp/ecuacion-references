package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.sle.controller;

import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.sle.form.PtSle21PtnEditForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.sle.service.PtSle21PtnEditService;
import jp.ecuacion.splib.web.controller.SplibEditController;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Scope("prototype")
@RequestMapping("/public/ptSle21Ptn/edit")
public class PtSle21PtnEditController
    extends SplibEditController<PtSle21PtnEditForm, PtSle21PtnEditService> {

  public PtSle21PtnEditController() {
    super(PageTemplatePatternEnum.PAIR_WITH_SEARCH_LIST, "ptSle21Ptn",
        newContext().mainRootRecordName("month").functionKinds("02-page"));
  }
}
