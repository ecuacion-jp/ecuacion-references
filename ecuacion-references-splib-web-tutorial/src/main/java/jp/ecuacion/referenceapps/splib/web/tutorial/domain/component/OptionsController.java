package jp.ecuacion.referenceapps.splib.web.tutorial.domain.component;

import jakarta.validation.Valid;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.OptionsController.OptionsForm;
import jp.ecuacion.splib.web.controller.SplibGeneral1FormController;
import jp.ecuacion.splib.web.form.SplibGeneralForm;
import jp.ecuacion.splib.web.service.SplibGeneral1FormDoNothingService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Scope("prototype")
@RequestMapping("/public/01-component/options")
public class OptionsController extends
    SplibGeneral1FormController<OptionsForm, SplibGeneral1FormDoNothingService<OptionsForm>> {

  public OptionsController() {
    super("options",
        newContext().functionKinds("01-component").mainRootRecordName("greeting"));
  }

  @PostMapping(value = "action", params = "greetingButton")
  public String greeting(Model model, @Validated OptionsForm form, BindingResult result)
      throws Exception {
    prepare(model, form.validate(result));

    form.getGreeting().setFirstName("John");

    return redirectToSamePageTakingOverModel(model, true);
  }

  /**
   * Provides a form for greeting page.
   * 
   * <p>It's okay for this class to be an independent one. This is just an saving of class files.</p>
   */
  public static class OptionsForm extends SplibGeneralForm {

    @Valid
    private GreetingWithFirstNameRecord greeting = new GreetingWithFirstNameRecord("John");

    public GreetingWithFirstNameRecord getGreeting() {
      return greeting;
    }

    public void setGreeting(GreetingWithFirstNameRecord greeting) {
      this.greeting = greeting;
    }
  }
}
