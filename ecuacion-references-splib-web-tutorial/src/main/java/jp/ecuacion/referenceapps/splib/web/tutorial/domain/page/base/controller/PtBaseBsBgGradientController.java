package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.base.controller;

import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.base.form.PtBaseBsBgGradientForm;
import jp.ecuacion.splib.web.controller.SplibGeneralController;
import jp.ecuacion.splib.web.service.SplibGeneralDoNothingService;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Scope("prototype")
@RequestMapping("/public/ptBaseBsBgGradient")
public class PtBaseBsBgGradientController
    extends SplibGeneralController<SplibGeneralDoNothingService> {

  public PtBaseBsBgGradientController() {
    super("ptBaseBsBgGradient", newContext().functionKinds("02-page"));
  }

  @GetMapping("page")
  public String page(Model model, PtBaseBsBgGradientForm form,
      @AuthenticationPrincipal UserDetails loginUser) throws Exception {

    prepare(model, loginUser, form);
    return getDefaultHtmlPageName();
  }

  @PostMapping(value = "action", params = "successMsg")
  public String successMsg(Model model, PtBaseBsBgGradientForm form,
      @AuthenticationPrincipal UserDetails loginUser) throws Exception {

    prepare(model, loginUser, form);
    return getRedirectUrlOnSuccess();
  }
}
