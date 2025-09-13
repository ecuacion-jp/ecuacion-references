package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.controller;

import jp.ecuacion.lib.core.exception.checked.AppException;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneral1FormMltRecordsForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service.PtGeneral1FormMltRecordsService;
import jp.ecuacion.splib.web.controller.SplibGeneralController;
import jp.ecuacion.splib.web.util.SplibUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Scope("prototype")
@RequestMapping("/public/ptGeneral1FormMltRecords")
public class PtGeneral1FormMltRecordsController
    extends SplibGeneralController<PtGeneral1FormMltRecordsService> {

  @Autowired
  SplibUtil util;

  public PtGeneral1FormMltRecordsController() {
    super("ptGeneral1FormMltRecords");
  }

  @GetMapping("page")
  public String page(Model model, PtGeneral1FormMltRecordsForm form) throws AppException {
    prepare(model, form);
    return getDefaultHtmlPageName();
  }

  @PostMapping(value = "action", params = "serverInfoBtn")
  public String serverTimestamp(Model model, PtGeneral1FormMltRecordsForm form) throws Exception {

    prepare(model, form);
    getService().getServerTimestamp(form, request.getLocale());

    // modelは画面でも使用するため引き継ぎ処理
    return redirectToSamePageTakingOverModel(model, true);
  }

  @PostMapping(value = "action", params = "greetingBtn")
  public String greeting(Model model, @Validated PtGeneral1FormMltRecordsForm form,
      BindingResult result) throws Exception {

    prepare(model, form.validate(result));
    getService().getGreeting(form);

    // modelは画面でも使用するため引き継ぎ処理
    return redirectToSamePageTakingOverModel(model, true);
  }

}
