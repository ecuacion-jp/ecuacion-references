package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.controller;

import jp.ecuacion.lib.core.exception.checked.BizLogicAppException;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneralFeaturesForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service.PtGeneralFeaturesService;
import jp.ecuacion.splib.web.bean.ReturnUrlBean;
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
@RequestMapping("/public/ptGeneralFeatures")
public class PtGeneralFeaturesController extends SplibGeneralController<PtGeneralFeaturesService> {

  @Autowired
  private SplibUtil util;

  public PtGeneralFeaturesController() {
    super("ptGeneralFeatures");
  }

  @GetMapping("page")
  public String page(Model model, PtGeneralFeaturesForm form) throws Exception {
    prepare(model, form);
    getService().page(form, getFunction());

    return getDefaultHtmlPageName();
  }

  @PostMapping(value = "action", params = "successMsg")
  public String successMsg(Model model, PtGeneralFeaturesForm form) throws Exception {

    prepare(model, form);

    return new ReturnUrlBean(this, util).showSuccessMessage().getUrl();
  }

  @PostMapping(value = "action", params = "appException")
  public String appException(Model model, PtGeneralFeaturesForm form) throws Exception {

    prepare(model, form);
    throw new BizLogicAppException("PT_GENERAL_FEATURES_APP_EXCEPTION_MSG", "abc");
  }

  @PostMapping(value = "action", params = "exclusiveControlFile")
  public String exclusiveControlFile(Model model, PtGeneralFeaturesForm form) throws Exception {

    prepare(model, form);
    getService().exclusiveControlFile(form, getFunction());

    return new ReturnUrlBean(this, util).showSuccessMessage().getUrl();
  }

  @PostMapping(value = "action", params = "validationTrue")
  public String validationTrue(Model model, @Validated PtGeneralFeaturesForm form,
      BindingResult result) throws Exception {

    prepare(model, form.validate(result));

    // modelは次画面でも使用するため引き継ぎ処理
    return redirectToSamePageTakingOverModel(model, true);
  }

  @PostMapping(value = "action", params = "validationFalse")
  public String validationFalse(Model model, PtGeneralFeaturesForm form) throws Exception {

    prepare(model, form);

    // modelは次画面でも使用するため引き継ぎ処理
    return redirectToSamePageTakingOverModel(model, true);
  }

  @PostMapping(value = "action", params = "warning")
  public String warning(Model model, PtGeneralFeaturesForm form) throws Exception {

    prepare(model, form);
    getService().warning(form);

    return new ReturnUrlBean(this, util).showSuccessMessage().getUrl();
  }

  @PostMapping(value = "action", params = "redirectOnSuccess")
  public String redirectPageOnSuccess(Model model, PtGeneralFeaturesForm form) throws Exception {

    prepare(model, form);
    return new ReturnUrlBean(this, util, "transitionTo").showSuccessMessage().getUrl();
  }

  @PostMapping(value = "action", params = "redirectOnAppException")
  public String redirectPageOnAppException(Model model, PtGeneralFeaturesForm form)
      throws Exception {

    redirectUrlOnAppExceptionBean = new ReturnUrlBean(this, util, false, "transitionTo");
    prepare(model, form);
    throw new BizLogicAppException("PT_GENERAL_FEATURES_REDIRECT_ON_FAIL_MSG");
  }

  /** redirect先URLとして使用するのでget。 */
  @GetMapping("transitionTo")
  public String transitionTo(Model model, PtGeneralFeaturesForm form) throws Exception {

    prepare(model, form);
    return "ptGeneralFeaturesTransitionTo";
  }
}

