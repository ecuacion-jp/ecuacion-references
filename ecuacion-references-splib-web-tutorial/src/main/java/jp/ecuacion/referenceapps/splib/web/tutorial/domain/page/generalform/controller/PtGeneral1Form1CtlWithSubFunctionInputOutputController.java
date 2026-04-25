package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.controller;

import jakarta.servlet.http.HttpServletRequest;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneral1Form1CtlWithSubFunctionInputOutputForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service.PtGeneral1Form1WithSubFunctionShowService;
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
@RequestMapping("/public/ptGeneral1Form1CtlWithSubFunction/inputOutput")
public class PtGeneral1Form1CtlWithSubFunctionInputOutputController
    extends SplibGeneralController<PtGeneral1Form1WithSubFunctionShowService> {

  @Autowired
  HttpServletRequest request;

  @Autowired
  SplibUtil util;

  public PtGeneral1Form1CtlWithSubFunctionInputOutputController() {
    super("ptGeneral1Form1CtlWithSubFunction",
        newContext().subFunction("inputOutput").mainRootRecordName("greeting"));
  }

  @GetMapping("page")
  /** Displays page. */
  public String page(Model model, PtGeneral1Form1CtlWithSubFunctionInputOutputForm form) {

    prepare(model, form);
    return getDefaultHtmlPageName();
  }

  @PostMapping(value = "action", params = "greetingBtn")
  public String greeting(Model model,
      @Validated PtGeneral1Form1CtlWithSubFunctionInputOutputForm form, BindingResult result)
      throws Exception {

    prepare(model, form.validate(result));
    getService().getGreeting(form);

    // modelは次画面でも使用するため引き継ぎ処理
    return redirectToSamePageTakingOverModel(model, true);
  }
}
