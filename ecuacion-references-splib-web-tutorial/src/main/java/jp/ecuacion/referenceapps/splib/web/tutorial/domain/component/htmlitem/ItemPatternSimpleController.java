package jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.htmlitem;

import jakarta.validation.Valid;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.GreetingWithFirstNameRecord;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.htmlitem.ItemPatternSimpleController.ItemPatternSimpleForm;
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

@Controller
@Scope("prototype")
@RequestMapping("/public/01-component/htmlItem/itemPatternSimple")
public class ItemPatternSimpleController extends
    SplibGeneral1FormController<ItemPatternSimpleForm, SplibGeneral1FormDoNothingService<ItemPatternSimpleForm>> {

  public ItemPatternSimpleController() {
    super("itemPatternSimple",
        newContext().functionKinds("01-component", "htmlItem").mainRootRecordName("greeting"));
  }

  @PostMapping(value = "action", params = "greetingButton")
  public String greeting(Model model, @Validated ItemPatternSimpleForm form,
      BindingResult result) throws Exception {
    prepare(model, form.validate(result));

    String firstName = form.getGreeting().getFirstName();
    String msg = String.format(StringUtils.isEmpty(firstName) ? "Hi!" : "Hi, %s!", firstName);
    form.getGreeting().setMessage(msg);

    return redirectToSamePageTakingOverModel(model, true);
  }

  /**
   * Provides a form for greeting page.
   * 
   * <p>It's okay for this class to be an independent one. This is just an saving of class files.</p>
   */
  public static class ItemPatternSimpleForm extends SplibGeneralForm {

    @Valid
    private GreetingWithFirstNameRecord greeting = new GreetingWithFirstNameRecord();

    public GreetingWithFirstNameRecord getGreeting() {
      return greeting;
    }

    public void setGreeting(GreetingWithFirstNameRecord greeting) {
      this.greeting = greeting;
    }
  }
}
