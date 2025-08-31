//package jp.ecuacion.referenceapps.splib.web.tutorial.domain.system;
//
//import jp.ecuacion.lib.core.exception.checked.AppException;
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
//@RequestMapping("/public/stCommonFeatures")
//public class StCommonFeaturesController
//    extends SplibGeneralController<SplibGeneralDoNothingService> {
//
//  public StCommonFeaturesController() {
//    super("stCommonFeatures");
//  }
//
//  @GetMapping("page")
//  public String page(Model model) throws AppException {
//    prepare(model, new StCommonFeaturesForm());
//    return getDefaultHtmlPageName();
//  }
//
//  @GetMapping(value = "action", params = "404")
//  public String method404(Model model) {
//
//    return "redirect:/public/notFound/page";
//  }
//
//  @GetMapping(value = "action", params = "accessDenied")
//  public String config(Model model) {
//
//    return "redirect:/account/loginHome/page";
//  }
//}
