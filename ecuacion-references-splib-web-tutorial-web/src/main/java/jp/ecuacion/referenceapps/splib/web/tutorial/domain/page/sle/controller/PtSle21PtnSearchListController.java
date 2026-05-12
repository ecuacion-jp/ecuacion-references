package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.sle.controller;

import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.sle.form.PtSle21PtnListForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.sle.form.PtSle21PtnSearchForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.sle.service.PtSle21PtnSearchListService;
import jp.ecuacion.splib.web.controller.SplibSearchListController;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

//@formatter:off
@Controller
@Scope("prototype")
@RequestMapping("/public/ptSle21Ptn/searchList")
public class PtSle21PtnSearchListController extends SplibSearchListController
    <PtSle21PtnSearchForm, PtSle21PtnListForm, PtSle21PtnSearchListService> {
  //@formatter:on

  public PtSle21PtnSearchListController() {
    super("ptSle21Ptn", newContext().mainRootRecordName("month").functionKinds("02-page"));
  }
}
