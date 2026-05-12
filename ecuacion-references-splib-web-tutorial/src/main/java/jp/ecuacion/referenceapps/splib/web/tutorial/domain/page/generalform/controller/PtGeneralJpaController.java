package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.controller;

import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneralJpaForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service.PtGeneralJpaService;
import jp.ecuacion.splib.web.controller.SplibGeneralController;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Scope("prototype")
@RequestMapping("/public/ptGeneralJpa")
public class PtGeneralJpaController extends SplibGeneralController<PtGeneralJpaService> {

  public PtGeneralJpaController() {
    super("ptGeneralJpa", newContext().mainRootRecordName("yourName").functionKinds("02-page/general-form"));
  }

  @GetMapping("page")
  public String page(Model model) throws Exception {
    PtGeneralJpaForm form = new PtGeneralJpaForm();
    prepare(model, form);
    getService().page(form, getFunction());
    return getDefaultHtmlPageName();
  }

  @PostMapping(value = "action", params = "update")
  public String update(Model model, PtGeneralJpaForm form) throws Exception {

    prepare(model, form);
    getService().action(form);

    return getRedirectUrlOnSuccess();
  }
}
