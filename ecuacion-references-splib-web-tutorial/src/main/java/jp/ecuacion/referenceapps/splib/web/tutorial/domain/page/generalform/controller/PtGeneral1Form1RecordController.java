package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.controller;

import jakarta.servlet.http.HttpServletRequest;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneral1Form1RecordForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service.PtGeneral1Form1RecordService;
import jp.ecuacion.splib.web.controller.SplibGeneralController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Scope("prototype")
@RequestMapping("/public/ptGeneral1Form1Record")
public class PtGeneral1Form1RecordController
    extends SplibGeneralController<PtGeneral1Form1RecordService> {

  @Autowired
  HttpServletRequest request;

  public PtGeneral1Form1RecordController() {
    super("ptGeneral1Form1Record", newContext().mainRootRecordName("greeting"));
  }

  @GetMapping("page")
  /** Displays page. */
  public String page(Model model, PtGeneral1Form1RecordForm form) {

    prepare(model, form);
    return getDefaultHtmlPageName();
  }

  @PostMapping(value = "action", params = "greetingBtn")
  public String greeting(Model model, @Validated PtGeneral1Form1RecordForm form,
      BindingResult result, RedirectAttributes redirectAttributes) throws Exception {

    prepare(model, form.validate(result));
    getService().getGreeting(form);

    // modelは次画面でも使用するため引き継ぎ処理
    return redirectToSamePageTakingOverModel(model, true, redirectAttributes);
  }
}
