//package jp.ecuacion.referenceapps.splib.web.tutorial.domain.system;
//
//import jp.ecuacion.splib.web.controller.SplibGeneralController;
//import jp.ecuacion.splib.web.service.SplibGeneralDoNothingService;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//@Scope("prototype")
//@RequestMapping("/public/stPagesProvidedByLibrary")
//public class StPagesProvidedByLibraryController
//    extends SplibGeneralController<SplibGeneralDoNothingService> {
//
//  public StPagesProvidedByLibraryController() {
//    super("stPagesProvidedByLibrary");
//  }
//
//  @GetMapping("page")
//  public String page(Model model) {
//    model.addAttribute("stPagesProvidedByLibraryForm", new StPagesProvidedByLibraryForm());
//    return getDefaultHtmlPageName();
//  }
//
//  @GetMapping(value = "action", params = "systemError")
//  public String systemError(Model model) {
//
//    return "error";
//  }
//
//  @GetMapping(value = "action", params = "config")
//  public String config(Model model) {
//
//    return "redirect:/ecuacion/public/config/page";
//  }
//}
