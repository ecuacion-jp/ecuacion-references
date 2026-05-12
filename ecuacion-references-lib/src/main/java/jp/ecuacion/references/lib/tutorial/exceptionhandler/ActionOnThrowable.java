package jp.ecuacion.references.lib.tutorial.exceptionhandler;

import java.util.Objects;
import jp.ecuacion.lib.core.util.MailUtil;
import jp.ecuacion.splib.core.exceptionhandler.SplibExceptionHandlerAction;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class ActionOnThrowable implements SplibExceptionHandlerAction {

  @Override
  public void execute(@Nullable Throwable th) {
    MailUtil.sendErrorMail(Objects.requireNonNull(th));
  }
}
