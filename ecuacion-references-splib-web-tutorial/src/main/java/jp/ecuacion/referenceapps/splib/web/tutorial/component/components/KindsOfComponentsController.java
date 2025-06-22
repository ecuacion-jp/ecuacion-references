package jp.ecuacion.referenceapps.splib.web.tutorial.component.components;

import jakarta.validation.Valid;
import jp.ecuacion.referenceapps.splib.web.tutorial.component.htmlItem.ItemNameKeyController.ItemNameKeyForm;
import jp.ecuacion.splib.core.form.record.SplibRecord;
import jp.ecuacion.splib.web.bean.HtmlItem;
import jp.ecuacion.splib.web.controller.SplibGeneral1FormController;
import jp.ecuacion.splib.web.form.SplibGeneralForm;
import jp.ecuacion.splib.web.form.record.RecordInterface;
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
@RequestMapping("/public/01-component/components/kindOfComponents")
public class KindsOfComponentsController extends
    SplibGeneral1FormController<ItemNameKeyForm, SplibGeneral1FormDoNothingService<ItemNameKeyForm>> {

  public KindsOfComponentsController() {
    super("kindsOfComponents",
        newContext().functionKinds("01-component", "components").mainRootRecordName("greeting"));
  }

  @PostMapping(value = "action", params = "greetingButton")
  public String greeting(Model model, @Validated KindsOfComponentsForm form, BindingResult result)
      throws Exception {
    prepare(model, form.validate(result));

    return redirectToSamePageTakingOverModel(model, true);
  }

  /**
   * Provides a form for greeting page.
   * 
   * <p>It's okay for this class to be an independent one. This is just an saving of class files.</p>
   */
  public static class KindsOfComponentsForm extends SplibGeneralForm {

    @Valid
    private MyRecord greeting = new MyRecord();

    public MyRecord getGreeting() {
      return greeting;
    }

    public void setGreeting(MyRecord greeting) {
      this.greeting = greeting;
    }
  }

  public static class MyRecord extends SplibRecord implements RecordInterface {

    // private String anotherFirstName1;

    @Override
    public HtmlItem[] getHtmlItems() {
      // TODO Auto-generated method stub
      return null;
    }
  }
}
