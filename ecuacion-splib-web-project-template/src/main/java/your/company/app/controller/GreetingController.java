package your.company.app.controller;

import jakarta.validation.Valid;
import jp.ecuacion.splib.web.controller.SplibGeneral1FormController;
import jp.ecuacion.splib.web.form.SplibGeneralForm;
import jp.ecuacion.splib.web.service.SplibGeneral1FormDoNothingService;
import org.apache.groovy.parser.antlr4.util.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import your.company.app.controller.GreetingController.GreetingForm;
import your.company.app.record.GreetingRecord;

@Controller
@Scope("prototype")
@RequestMapping("/public/greeting")
public class GreetingController extends
    SplibGeneral1FormController<GreetingForm, SplibGeneral1FormDoNothingService<GreetingForm>> {

  public GreetingController() {
    super("greeting");

  }

  @PostMapping(value = "action", params = "greeting")
  public String greeting(Model model, @Validated GreetingForm form, BindingResult result)
      throws Exception {
    prepare(model, form);

    String firstName = form.getGreeting().getFirstName();
    String msg = String.format(StringUtils.isEmpty(firstName) ? "Hi!" : "Hi, %s!", firstName);
    form.getGreeting().setMessage(msg);

    return redirectToSamePageTakingOverModel(model, true);
  }

  public static class GreetingForm extends SplibGeneralForm {

    @Valid
    private GreetingRecord greeting = new GreetingRecord();

    public GreetingRecord getGreeting() {
      return greeting;
    }

    public void setGreeting(GreetingRecord greeting) {
      this.greeting = greeting;
    }
  }
}
