package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.controller;

import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneralMltForms1CtlWithSubFunctionInputForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneralMltForms1CtlWithSubFunctionOutputForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service.PtGeneralMltForms1CtlWithSubFunctionInputOutputService;
import jp.ecuacion.splib.web.controller.SplibGeneralController;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 1 controllerに複数formがある場合は難しいのだが、 generic
 * parameterのformは現時点ではinputよりの機能（submitOnChangeToRefresh）に使用されていることから inputFormを渡しておく。
 */
@Controller
@Scope("prototype")
@RequestMapping("/public/ptGeneralMltForms1CtlWithSubFunction/inputOutput")
public class PtGeneralMltForms1CtlWithSubFunctionInputOutputController
    extends SplibGeneralController<PtGeneralMltForms1CtlWithSubFunctionInputOutputService> {

  public PtGeneralMltForms1CtlWithSubFunctionInputOutputController() {
    super("ptGeneralMltForms1CtlWithSubFunction",
        newContext().subFunction("inputOutput").mainRootRecordName("greeting"));
  }

  @GetMapping(value = "page")
  /** Displays page. */
  public String page(Model model, PtGeneralMltForms1CtlWithSubFunctionInputForm inputForm,
      PtGeneralMltForms1CtlWithSubFunctionOutputForm outputForm) {

    prepare(model, inputForm, outputForm);
    return getDefaultHtmlPageName();
  }

  @PostMapping(value = "action", params = "greetingBtn")
  public String greeting(Model model,
      @Validated PtGeneralMltForms1CtlWithSubFunctionInputForm inputForm, BindingResult result,
      RedirectAttributes redirectAttributes) throws Exception {

    PtGeneralMltForms1CtlWithSubFunctionOutputForm outputForm =
        new PtGeneralMltForms1CtlWithSubFunctionOutputForm();
    prepare(model, inputForm.validate(result), outputForm);

    getService().getGreeting(inputForm, outputForm);

    // modelは次画面でも使用するため引き継ぎ処理
    return redirectToSamePageTakingOverModel(model, true, redirectAttributes);
  }

}
