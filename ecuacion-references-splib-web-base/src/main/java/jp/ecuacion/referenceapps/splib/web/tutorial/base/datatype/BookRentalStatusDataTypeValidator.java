package jp.ecuacion.referenceapps.splib.web.tutorial.base.datatype;

import jakarta.validation.*;
import java.lang.annotation.*;
import jp.ecuacion.lib.validation.constraints.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Constraint(validatedBy = {})
@SizeString(min = 1, max = 30)
@PatternWithDescription(regexp = "^[^!\"#\\$%&\\(\\)=\\^~\\\\\\|`\\[\\{;\\+:\\\\*\\]\\},<>/\\?]*$", description = "prohibitedChars")
public @interface BookRentalStatusDataTypeValidator {

  String message() default "";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};

}
