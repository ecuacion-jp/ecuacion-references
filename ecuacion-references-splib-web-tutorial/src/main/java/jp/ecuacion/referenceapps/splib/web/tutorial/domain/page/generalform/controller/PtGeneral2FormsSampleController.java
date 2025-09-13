package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.controller;

import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneral2FormsSampleInputForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneral2FormsSampleOutputForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service.PtGeneral2FormsSampleService;
import jp.ecuacion.splib.web.controller.SplibGeneral2FormsController;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//@formatter:off
@Controller
@Scope("prototype")
@RequestMapping("/public/ptGeneral2FormsSample")
public class PtGeneral2FormsSampleController extends
    SplibGeneral2FormsController<PtGeneral2FormsSampleInputForm, 
    PtGeneral2FormsSampleOutputForm, PtGeneral2FormsSampleService> {
  //@formatter:on

  public PtGeneral2FormsSampleController() {
    super("ptGeneral2FormsSample", newContext().mainRootRecordName("greeting"));
  }

  @PostMapping(value = "action", params = "greetingBtn")
  public String greeting(Model model, @Validated PtGeneral2FormsSampleInputForm inputForm,
      BindingResult result) throws Exception {
    PtGeneral2FormsSampleOutputForm outputForm = new PtGeneral2FormsSampleOutputForm();
    prepare(model, inputForm.validate(result), outputForm);
    getService().getGreeting(inputForm, outputForm);

    // modelは画面でも使用するため引き継ぎ処理
    return redirectToSamePageTakingOverModel(model, true);
  }

}
