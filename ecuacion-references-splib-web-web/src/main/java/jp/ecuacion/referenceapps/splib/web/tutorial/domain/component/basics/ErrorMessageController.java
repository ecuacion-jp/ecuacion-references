package jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.basics;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.basics.ErrorMessageController.ErrorMessageForm;
import jp.ecuacion.splib.core.record.SplibRecord;
import jp.ecuacion.splib.web.controller.SplibGeneral1FormController;
import jp.ecuacion.splib.web.form.SplibGeneralForm;
import jp.ecuacion.splib.web.item.HtmlItem;
import jp.ecuacion.splib.web.item.HtmlItemContainer;
import jp.ecuacion.splib.web.service.SplibGeneral1FormDoNothingService;
import org.apache.groovy.parser.antlr4.util.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Scope("prototype")
@RequestMapping("/public/01-component/basics/errorMessage")
//@formatter:off
public class ErrorMessageController extends SplibGeneral1FormController<ErrorMessageForm,
    SplibGeneral1FormDoNothingService<ErrorMessageForm>> {
  //@formatter:on

  public ErrorMessageController() {
    super("errorMessage",
        newContext().functionKinds("01-component", "basics").mainRootRecordName("greeting"));
  }

  @PostMapping(value = "action", params = "greetinga")
  public String greeting(Model model, @Validated ErrorMessageForm form, BindingResult result,
      RedirectAttributes redirectAttributes) throws Exception {
    prepare(model, form.validate(result));

    String firstName = form.getGreeting().getFirstName();
    String msg = StringUtils.isEmpty(firstName) ? "Hi!" : String.format("Hi, %s!", firstName);
    form.getGreeting().setMessage(msg);

    return redirectToSamePageTakingOverModel(model, true, redirectAttributes);
  }

  /**
   * Provides a form for greeting page.
   * 
   * <p>It's okay for this class to be an independent one. 
   *     This is just an saving of class files.</p>
   */
  public static class ErrorMessageForm extends SplibGeneralForm {

    @Valid
    private GreetingRecord greeting = new GreetingRecord();

    public GreetingRecord getGreeting() {
      return greeting;
    }

    public void setGreeting(GreetingRecord greeting) {
      this.greeting = greeting;
    }
  }

  public static class GreetingRecord extends SplibRecord implements HtmlItemContainer {

    @NotEmpty
    @Pattern(regexp = ".*[X-Z].*")
    @Size(min = 10, max = 12)
    private String firstName;
    private String message;

    public String getFirstName() {
      return firstName;
    }

    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    @Override
    public HtmlItem[] customizedItems() {
      return new HtmlItem[] {};
    }
  }
}
