package jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.components.generalformpage;

import jakarta.validation.Valid;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.components.generalformpage.ButtonController.ButtonForm;
import jp.ecuacion.splib.core.record.SplibRecord;
import jp.ecuacion.splib.web.controller.SplibGeneral1FormController;
import jp.ecuacion.splib.web.form.SplibGeneralForm;
import jp.ecuacion.splib.web.item.HtmlItem;
import jp.ecuacion.splib.web.item.HtmlItemContainer;
import jp.ecuacion.splib.web.service.SplibGeneral1FormDoNothingService;
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
@RequestMapping("/public/01-component/components/general-form-page/button")
public class ButtonController
    extends SplibGeneral1FormController<ButtonForm, SplibGeneral1FormDoNothingService<ButtonForm>> {

  public ButtonController() {
    super("button", newContext().functionKinds("01-component", "components", "general-form-page"));
  }

  @PostMapping(value = "action", params = "button")
  public String execute(Model model, @Validated ButtonForm form, BindingResult result,
      RedirectAttributes redirectAttributes) throws Exception {

    return redirectToSamePageTakingOverModel(model, true, redirectAttributes);
  }

  public static class ButtonForm extends SplibGeneralForm {

    @Valid
    private ButtonRecord button = new ButtonRecord();

    public ButtonRecord getButton() {
      return button;
    }

    public void setButton(ButtonRecord input) {
      this.button = input;
    }
  }

  public static class ButtonRecord extends SplibRecord implements HtmlItemContainer {

    @Override
    public HtmlItem[] customizedItems() {
      return new HtmlItem[] {};
    }
  }
}
