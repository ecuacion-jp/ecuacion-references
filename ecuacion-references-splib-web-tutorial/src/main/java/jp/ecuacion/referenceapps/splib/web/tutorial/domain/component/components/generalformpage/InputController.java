package jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.components.generalformpage;

import jakarta.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.components.generalformpage.InputController.InputForm;
import jp.ecuacion.splib.core.record.SplibRecord;
import jp.ecuacion.splib.web.bean.HtmlItem;
import jp.ecuacion.splib.web.bean.HtmlItemNumber;
import jp.ecuacion.splib.web.controller.SplibGeneral1FormController;
import jp.ecuacion.splib.web.form.SplibGeneralForm;
import jp.ecuacion.splib.web.form.record.RecordInterface;
import jp.ecuacion.splib.web.service.SplibGeneral1FormDoNothingService;
import jp.ecuacion.splib.web.util.SplibComponentUtil;
import org.apache.groovy.parser.antlr4.util.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
@Scope("prototype")
@RequestMapping("/public/01-component/components/general-form-page/input")
public class InputController
    extends SplibGeneral1FormController<InputForm, SplibGeneral1FormDoNothingService<InputForm>> {

  public InputController() {
    super("input", newContext().functionKinds("01-component", "components", "general-form-page"));
  }

  @PostMapping(value = "action", params = "button")
  public String execute(Model model, @Validated InputForm form, BindingResult result)
      throws Exception {

    // Save uploaded file here because large tmpFile will disappear after the execution of "prepare"
    // method.
    String tmpFilePathFile = SplibComponentUtil.saveUploadedFile(form.getInput().getInputFile());
    String tmpFilePathMobile =
        SplibComponentUtil.saveUploadedFile(form.getInput().getInputTakenPhotoMobile());
    final String tmpFilePathPc =
        SplibComponentUtil.saveUploadedFile(form.getInput().getInputTakenPhotoPc());

    prepare(model, form.validate(result));

    if (!StringUtils.isEmpty(tmpFilePathFile) && new File(tmpFilePathFile).exists()) {
      Files.delete(Path.of(tmpFilePathFile));
    }

    form.getInput()
        .setInputTakenPhotoMobileRegisteredBase64(readPhotoInBase64Format(tmpFilePathMobile));
    form.getInput().setInputTakenPhotoPcRegisteredBase64(readPhotoInBase64Format(tmpFilePathPc));

    return redirectToSamePageTakingOverModel(model, true);
  }

  private String readPhotoInBase64Format(String path) throws Exception {
    String rtn = null;
    if (path != null) {
      rtn = SplibComponentUtil.getPictureDataBase64(path, "jpeg");

      Files.delete(Path.of(path));
    }

    return rtn;
  }

  /**
   * Provides a form for greeting page.
   * 
   * <p>It's okay for this class to be an independent one. 
   *     This is just an saving of class files.</p>
   */
  public static class InputForm extends SplibGeneralForm {

    @Valid
    private InputRecord input = new InputRecord();

    public InputRecord getInput() {
      return input;
    }

    public void setInput(InputRecord input) {
      this.input = input;
    }
  }

  public static class InputRecord extends SplibRecord implements RecordInterface {
    private String inputText;
    private String inputPassword;
    private String inputHidden;
    private String inputNumber;
    private String inputYearMonth;
    private String inputDate;
    private String inputTime;
    private String inputTextArea;
    private String inputSwitch;
    private String inputCheckboxes;
    private String inputSelect;
    private String inputSelectWithLocale;
    private String inputSelectFromEnum;
    private MultipartFile inputFile;
    private MultipartFile inputTakenPhotoMobile;
    private String inputTakenPhotoMobileRegisteredBase64;
    private String inputTakenPhotoPc;
    private String inputTakenPhotoPcRegisteredBase64;

    private List<String[]> inputSelectionList =
        Arrays.asList(new String[][] {new String[] {"A", "selection-A"},
            new String[] {"B", "selection-B"}, new String[] {"C", "selection-C"}});

    static {
      getStringLengthMap().put("inputText", 3);
    }

    @Override
    public HtmlItem[] getHtmlItems() {
      return new HtmlItem[] {new HtmlItemNumber("inputNumber").needsCommas(true)};
    }

    public String getInputText() {
      return inputText;
    }

    public void setInputText(String inputText) {
      this.inputText = inputText;
    }

    public String getInputPassword() {
      return inputPassword;
    }

    public void setInputPassword(String inputPassword) {
      this.inputPassword = inputPassword;
    }

    public String getInputHidden() {
      return inputHidden;
    }

    public void setInputHidden(String inputHidden) {
      this.inputHidden = inputHidden;
    }

    public String getInputNumber() {
      return inputNumber;
    }

    public void setInputNumber(String inputNumber) {
      this.inputNumber = inputNumber;
    }

    public String getInputYearMonth() {
      return inputYearMonth;
    }

    public void setInputYearMonth(String inputYearMonth) {
      this.inputYearMonth = inputYearMonth;
    }

    public String getInputDate() {
      return inputDate;
    }

    public void setInputDate(String inputDate) {
      this.inputDate = inputDate;
    }

    public String getInputTime() {
      return inputTime;
    }

    public void setInputTime(String inputTime) {
      this.inputTime = inputTime;
    }

    public String getInputTextArea() {
      return inputTextArea;
    }

    public void setInputTextArea(String inputTextArea) {
      this.inputTextArea = inputTextArea;
    }

    public String getInputSwitch() {
      return inputSwitch;
    }

    public void setInputSwitch(String inputSwitch) {
      this.inputSwitch = inputSwitch;
    }

    public String getInputCheckboxes() {
      return inputCheckboxes;
    }

    public void setInputCheckboxes(String inputCheckboxes) {
      this.inputCheckboxes = inputCheckboxes;
    }

    public String getInputSelect() {
      return inputSelect;
    }

    public void setInputSelect(String inputSelect) {
      this.inputSelect = inputSelect;
    }

    public String getInputSelectWithLocale() {
      return inputSelectWithLocale;
    }

    public void setInputSelectWithLocale(String inputSelectWithLocale) {
      this.inputSelectWithLocale = inputSelectWithLocale;
    }

    public String getInputSelectFromEnum() {
      return inputSelectFromEnum;
    }

    public void setInputSelectFromEnum(String inputSelectFromEnum) {
      this.inputSelectFromEnum = inputSelectFromEnum;
    }

    public MultipartFile getInputFile() {
      return inputFile;
    }

    public void setInputFile(MultipartFile inputFile) {
      this.inputFile = inputFile;
    }

    public List<String[]> getInputCheckboxesList() {
      return inputSelectionList;
    }

    public List<String[]> getInputSelectList() {
      return inputSelectionList;
    }

    public List<String[]> getInputSelectWithLocaleList(Locale locale, String options) {
      return inputSelectionList;
    }

    public List<String[]> getInputSelectFromEnumList(Locale locale, String options) {
      return inputSelectionList;
    }

    public MultipartFile getInputTakenPhotoMobile() {
      return inputTakenPhotoMobile;
    }

    public void setInputTakenPhotoMobile(MultipartFile inputTakenPhotoMobile) {
      this.inputTakenPhotoMobile = inputTakenPhotoMobile;
    }

    public String getInputTakenPhotoMobileRegisteredBase64() {
      return inputTakenPhotoMobileRegisteredBase64;
    }

    public void setInputTakenPhotoMobileRegisteredBase64(
        String inputTakenPhotoMobileRegisteredBase64) {
      this.inputTakenPhotoMobileRegisteredBase64 = inputTakenPhotoMobileRegisteredBase64;
    }

    public String getInputTakenPhotoPc() {
      return inputTakenPhotoPc;
    }

    public void setInputTakenPhotoPc(String inputTakenPhotoPc) {
      this.inputTakenPhotoPc = inputTakenPhotoPc;
    }

    public String getInputTakenPhotoPcRegisteredBase64() {
      return inputTakenPhotoPcRegisteredBase64;
    }

    public void setInputTakenPhotoPcRegisteredBase64(String inputTakenPhotoPcRegisteredBase64) {
      this.inputTakenPhotoPcRegisteredBase64 = inputTakenPhotoPcRegisteredBase64;
    }
  }
}
