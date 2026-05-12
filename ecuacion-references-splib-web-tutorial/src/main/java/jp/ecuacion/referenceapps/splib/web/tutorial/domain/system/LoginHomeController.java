package jp.ecuacion.referenceapps.splib.web.tutorial.domain.system;

import jp.ecuacion.splib.web.controller.SplibBaseController;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Scope("prototype")
@RequestMapping("/account/loginHome")
public class LoginHomeController extends SplibBaseController {

  @GetMapping("page")
  public String page(Model model) {
    return "loginHome";
  }
}
