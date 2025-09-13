package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.controller;

import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form.PtGeneralMltFormsMltCtlsOutputForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.service.PtGeneralMultipleFormsMultipleCtlsOutputService;
import jp.ecuacion.splib.web.bean.MessagesBean;
import jp.ecuacion.splib.web.bean.ReturnUrlBean;
import jp.ecuacion.splib.web.constant.SplibWebConstants;
import jp.ecuacion.splib.web.controller.SplibGeneralController;
import jp.ecuacion.splib.web.util.SplibUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Scope("prototype")
@RequestMapping("/public/ptGeneralMltFormsMltCtls/output")
public class PtGeneralMltFormsMltCtlsOutputController
    extends SplibGeneralController<PtGeneralMultipleFormsMultipleCtlsOutputService> {

  @Autowired
  private SplibUtil util;

  public PtGeneralMltFormsMltCtlsOutputController() {
    super("ptGeneralMltFormsMltCtls",
        newContext().subFunction("output").htmlFilenamePostfix("").mainRootRecordName("greeting"));
  }

  @RequestMapping(value = "page", method = {RequestMethod.POST, RequestMethod.GET})
  public String page(Model model, PtGeneralMltFormsMltCtlsOutputForm outputForm) throws Exception {

    prepare(model, outputForm);
    MessagesBean bean = (MessagesBean) model.getAttribute(SplibWebConstants.KEY_MESSAGES_BEAN);

    // エラーがある場合は処理結果を表示しない
    if (bean.getErrorMessages() == null || bean.getErrorMessages().size() == 0) {
      getService().getGreeting(outputForm);
    }

    model.addAttribute("ptGeneralMltFormsMltCtlsOutputForm", outputForm);

    return getDefaultHtmlPageName();
  }

  @RequestMapping(value = "action", params = "forwarded",
      method = {RequestMethod.POST, RequestMethod.GET})
  public String action(Model model, PtGeneralMltFormsMltCtlsOutputForm outputForm)
      throws Exception {

    prepare(model, outputForm);
    // データ更新などの処理は特にないので、redirectするのみとする
    // modelは次画面でも使用するため引き継ぎ処理
    ReturnUrlBean bean = new ReturnUrlBean(this, util, "input", "page").showSuccessMessage()
        .putParam("greeting.name", outputForm.getGreeting().getName());
    return util.prepareForPageTransition(request, bean, model, false);
  }
}
