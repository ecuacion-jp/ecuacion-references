package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.controller;

import jp.ecuacion.lib.core.exception.checked.AppException;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneralMltFormsMltCtlsInputForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service.PtGeneralMltFormsMltCtlsInputService;
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
@RequestMapping("/public/ptGeneralMltFormsMltCtls/input")
public class PtGeneralMltFormsMltCtlsInputController
    extends SplibGeneralController<PtGeneralMltFormsMltCtlsInputService> {

  private static final String forwardSubFunction = "output";

  @Autowired
  private SplibUtil util;

  public PtGeneralMltFormsMltCtlsInputController() {
    super("ptGeneralMltFormsMltCtls",
        newContext().subFunction("input").mainRootRecordName("greeting"));
  }

  @GetMapping(value = "page")
  public String page(Model model, PtGeneralMltFormsMltCtlsInputForm inputForm) throws AppException {
    prepare(model, inputForm);

    // エラーメッセージがある場合はそれをoutputControllerに引き継ぐ必要があるが、forwardではmodelは引き継がれないため引き継ぎ処理を行う。
    ReturnUrlBean bean = (ReturnUrlBean) new ReturnUrlBean(this, util, forwardSubFunction, "page")
        .setProtocolForward();

    // modelは次画面でも使用するため引き継ぎ処理
    return util.prepareForPageTransition(request, bean, model, true);
  }

  @PostMapping(value = "action", params = "greetingBtn")
  public String greeting(Model model, @Validated PtGeneralMltFormsMltCtlsInputForm inputForm,
      BindingResult result) throws Exception {

    prepare(model, inputForm.validate(result));
    getService().validationCheck(inputForm);

    // modelは次画面でも使用するため引き継ぎ処理
    ReturnUrlBean bean =
        new ReturnUrlBean(this, util, forwardSubFunction, "action").setProtocolForward();
    return util.prepareForPageTransition(request, bean, model, false);
  }
}
