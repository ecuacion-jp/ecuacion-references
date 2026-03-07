package jp.ecuacion.referenceapps.splib.web.tutorial.domain.system;

import jakarta.validation.Valid;
import jp.ecuacion.lib.core.exception.checked.AppException;
import jp.ecuacion.splib.core.record.SplibRecord;
import jp.ecuacion.splib.web.controller.SplibGeneralController;
import jp.ecuacion.splib.web.form.SplibGeneralForm;
import jp.ecuacion.splib.web.item.HtmlItem;
import jp.ecuacion.splib.web.item.HtmlItemContainer;
import jp.ecuacion.splib.web.service.SplibGeneralDoNothingService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Scope("prototype")
@RequestMapping("/public/03-system/stCommonFeatures")
public class StCommonFeaturesController
    extends SplibGeneralController<SplibGeneralDoNothingService> {

  public StCommonFeaturesController() {
    super("stCommonFeatures", newContext().functionKinds("03-system"));
  }

  @GetMapping("page")
  public String page(Model model) throws AppException {
    prepare(model, new StCommonFeaturesForm());
    return getDefaultHtmlPageName();
  }

  @GetMapping(value = "action", params = "404")
  public String method404(Model model) {

    return "redirect:/public/notFound/page";
  }

  @GetMapping(value = "action", params = "accessDenied")
  public String config(Model model) {

    return "redirect:/account/loginHome/page";
  }
  
  public static class StCommonFeaturesForm extends SplibGeneralForm {

    @Valid
    private StCommonFeaturesRecord stCommonFunctions = new StCommonFeaturesRecord();

    public StCommonFeaturesRecord getStCommonFunctions() {
      return stCommonFunctions;
    }

    public void setStCommonFunctions(StCommonFeaturesRecord stCommonFunctions) {
      this.stCommonFunctions = stCommonFunctions;
    }
  }
  
  public static class StCommonFeaturesRecord extends SplibRecord implements HtmlItemContainer {

    @Override
    public HtmlItem[] getHtmlItems() {
      return new HtmlItem[] {};
    }

  }

}
