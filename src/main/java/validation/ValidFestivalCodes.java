package validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FestivalCodesValidator.class)
public @interface ValidFestivalCodes {
	String message() default "Festivalcodes zijn ongeldig: code1 moet even zijn, code2 deelbaar door 3 en verschil < 300.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
