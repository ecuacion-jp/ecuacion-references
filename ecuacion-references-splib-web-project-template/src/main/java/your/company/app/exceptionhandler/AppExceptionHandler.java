package your.company.app.exceptionhandler;

import jakarta.servlet.http.HttpServletRequest;
import jp.ecuacion.splib.core.exceptionhandler.SplibExceptionHandlerAction;
import jp.ecuacion.splib.web.exceptionhandler.SplibExceptionHandler;
import jp.ecuacion.splib.web.util.SplibLoginStateUtil;
import org.jspecify.annotations.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class AppExceptionHandler extends SplibExceptionHandler {

  /**
   * Constructs a new instance.
   */
  public AppExceptionHandler(HttpServletRequest request,
      @Nullable SplibExceptionHandlerAction actionOnThrowable,
      SplibLoginStateUtil loginStateUtil) {
    super(request, actionOnThrowable, loginStateUtil);
  }
}
