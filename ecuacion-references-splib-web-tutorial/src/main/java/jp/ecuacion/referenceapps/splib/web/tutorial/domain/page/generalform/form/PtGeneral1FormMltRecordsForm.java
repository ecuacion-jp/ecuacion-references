package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.form;

import jakarta.validation.Valid;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record.GreetingRecord;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record.ServerInfoRecord;
import jp.ecuacion.splib.web.form.SplibGeneralForm;

public class PtGeneral1FormMltRecordsForm extends SplibGeneralForm {


  @Valid
  private GreetingRecord greeting = new GreetingRecord();

  private ServerInfoRecord serverInfo = new ServerInfoRecord();

  public GreetingRecord getGreeting() {
    return greeting;
  }

  public void setGreeting(GreetingRecord greeting) {
    this.greeting = greeting;
  }

  public ServerInfoRecord getServerInfo() {
    return serverInfo;
  }

  public void setServerInfo(ServerInfoRecord serverInfo) {
    this.serverInfo = serverInfo;
  }
}
