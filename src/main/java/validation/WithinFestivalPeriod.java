package validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jvnet.staxex.StAxSOAPBody.Payload;

import jakarta.validation.Constraint;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = WithinFestivalPeriodValidator.class)
public @interface WithinFestivalPeriod {
	String message() default "Datums vallen buiten de festivalperiode.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String startField();

	String endField();
}
