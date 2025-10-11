package jp.ecuacion.referenceapps.splib.web.tutorial.domain.system;

import jakarta.validation.Valid;
import jp.ecuacion.splib.core.record.SplibRecord;
import jp.ecuacion.splib.web.bean.HtmlItem;
import jp.ecuacion.splib.web.controller.SplibGeneralController;
import jp.ecuacion.splib.web.form.SplibGeneralForm;
import jp.ecuacion.splib.web.form.record.RecordInterface;
import jp.ecuacion.splib.web.service.SplibGeneralDoNothingService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Scope("prototype")
@RequestMapping("/public/03-system/stPagesProvidedByLibrary")
public class StPagesProvidedByLibraryController
    extends SplibGeneralController<SplibGeneralDoNothingService> {

  public StPagesProvidedByLibraryController() {
    super("stPagesProvidedByLibrary", newContext().functionKinds("03-system"));
  }

  @GetMapping("page")
  public String page(Model model) {
    model.addAttribute("stPagesProvidedByLibraryForm", new StPagesProvidedByLibraryForm());
    return getDefaultHtmlPageName();
  }

  @GetMapping(value = "action", params = "systemError")
  public String systemError(Model model) {

    return "error";
  }

  @GetMapping(value = "action", params = "config")
  public String config(Model model) {

    return "redirect:/ecuacion/public/config/page";
  }

  public static class StPagesProvidedByLibraryForm extends SplibGeneralForm {

    @Valid
    private StPagesProvidedByLibraryRecord stPagesProvidedByLibrary =
        new StPagesProvidedByLibraryRecord();

    public StPagesProvidedByLibraryRecord getStPagesProvidedByLibrary() {
      return stPagesProvidedByLibrary;
    }

    public void setStPagesProvidedByLibrary(
        StPagesProvidedByLibraryRecord stPagesProvidedByLibrary) {
      this.stPagesProvidedByLibrary = stPagesProvidedByLibrary;
    }
  }

  public static class StPagesProvidedByLibraryRecord extends SplibRecord
      implements RecordInterface {

    @Override
    public HtmlItem[] getHtmlItems() {
      return new HtmlItem[] {};
    }

  }
}
