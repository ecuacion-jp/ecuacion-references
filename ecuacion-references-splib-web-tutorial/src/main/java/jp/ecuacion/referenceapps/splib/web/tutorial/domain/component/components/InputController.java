package jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.components;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.component.components.InputController.InputForm;
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
@RequestMapping("/public/01-component/components/input")
public class InputController extends
    SplibGeneral1FormController<InputForm, SplibGeneral1FormDoNothingService<InputForm>> {

  public InputController() {
    super("input",
        newContext().functionKinds("01-component", "components"));
  }

  @PostMapping(value = "action", params = "button")
  public String execute(Model model, @Validated InputForm form, BindingResult result)
      throws Exception {
    prepare(model, form.validate(result));

    return redirectToSamePageTakingOverModel(model, true);
  }

  /**
   * Provides a form for greeting page.
   * 
   * <p>It's okay for this class to be an independent one. This is just an saving of class files.</p>
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
    private String inputSelectFromEnum;
    private String inputFile;
    private String inputTakenPhoto;

    private List<String[]> inputCheckboxesList = Arrays.asList(new String[][] {
      new String[] {"A", "selection-A"}, new String[] {"B", "selection-B"}, new String[] {"C", "selection-C"}});
    private List<String[]> inputSelectList = Arrays.asList(new String[][] {
      new String[] {"A", "selection-A"}, new String[] {"B", "selection-B"}, new String[] {"C", "selection-C"}});

    @Override
    public HtmlItem[] getHtmlItems() {
      return new HtmlItem[] {new HtmlItem("inputText").isNotEmpty(true)};
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

    public String getInputSelectFromEnum() {
      return inputSelectFromEnum;
    }

    public void setInputSelectFromEnum(String inputSelectFromEnum) {
      this.inputSelectFromEnum = inputSelectFromEnum;
    }

    public String getInputFile() {
      return inputFile;
    }

    public void setInputFile(String inputFile) {
      this.inputFile = inputFile;
    }

    public String getInputTakenPhoto() {
      return inputTakenPhoto;
    }

    public void setInputTakenPhoto(String inputTakenPhoto) {
      this.inputTakenPhoto = inputTakenPhoto;
    }

    public List<String[]> getInputCheckboxesList() {
      return inputCheckboxesList;
    }

    public void setInputCheckboxesList(List<String[]> inputCheckboxesList) {
      this.inputCheckboxesList = inputCheckboxesList;
    }
    
    public List<String[]> getInputSelectList() {
      return inputSelectList;
    }

    public void setInputSelectList(List<String[]> inputSelectList) {
      this.inputSelectList = inputSelectList;
    }

  }
}
