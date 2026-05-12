package jp.ecuacion.referenceapps.splib.web.tutorial.exceptionhandler;

import jakarta.annotation.Nonnull;
import jp.ecuacion.lib.core.util.MailUtil;
import jp.ecuacion.splib.core.exceptionhandler.SplibExceptionHandlerAction;
import org.springframework.stereotype.Component;

@Component
public class ActionOnThrowable implements SplibExceptionHandlerAction {

  @Override
  public void execute(@Nonnull Throwable th) {
    MailUtil.sendErrorMail(th);
  }
}
