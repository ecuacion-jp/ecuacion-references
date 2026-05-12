package jp.ecuacion.referenceapps.splib.web.tutorial.exceptionhandler;

import jakarta.servlet.http.HttpServletRequest;
import jp.ecuacion.splib.core.exceptionhandler.SplibExceptionHandlerAction;
import jp.ecuacion.splib.web.jpa.exceptionhandler.SplibJpaExceptionHandler;
import jp.ecuacion.splib.web.util.SplibLoginStateUtil;
import org.jspecify.annotations.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class AppExceptionHandlerJpa extends SplibJpaExceptionHandler {

  protected AppExceptionHandlerJpa(HttpServletRequest request,
      @Nullable SplibExceptionHandlerAction actionOnThrowable,
      SplibLoginStateUtil loginStateUtil) {
    super(request, actionOnThrowable, loginStateUtil);
  }
}
