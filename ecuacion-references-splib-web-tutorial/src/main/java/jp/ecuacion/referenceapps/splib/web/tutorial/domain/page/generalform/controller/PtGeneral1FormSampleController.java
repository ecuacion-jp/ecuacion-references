package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.controller;

import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneral1FormSampleForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service.PtGeneral1FormSampleService;
import jp.ecuacion.splib.web.controller.SplibGeneral1FormController;
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
@RequestMapping("/public/ptGeneral1FormSample")
public class PtGeneral1FormSampleController
    extends SplibGeneral1FormController<PtGeneral1FormSampleForm, PtGeneral1FormSampleService> {
  //@formatter:on
  
  public PtGeneral1FormSampleController() {
    super("ptGeneral1FormSample", newContext().mainRootRecordName("greeting"));
  }

  @PostMapping(value = "action", params = "greetingBtn")
  public String greeting(Model model, @Validated PtGeneral1FormSampleForm form,
      BindingResult result) throws Exception {

    prepare(model, form.validate(result));
    getService().getGreeting(form);

    // modelは画面でも使用するため引き継ぎ処理
    return redirectToSamePageTakingOverModel(model, true);
  }
}
