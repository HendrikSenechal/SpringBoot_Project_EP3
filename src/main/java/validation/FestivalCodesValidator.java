package validation;

import entity.Festival;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FestivalCodesValidator implements ConstraintValidator<ValidFestivalCodes, Festival> {

	@Override
	public boolean isValid(Festival festival, ConstraintValidatorContext context) {
		if (festival == null)
			return true;

		int code1 = festival.getFestivalCode1();
		int code2 = festival.getFestivalCode2();

		boolean isValid = true;

		// Reset default message if needed
		context.disableDefaultConstraintViolation();

		// Check even
		if (code1 <= 0 || code1 % 2 != 0) {
			context.buildConstraintViolationWithTemplate("FestivalCode1 moet een strikt positief en even getal zijn.")
					.addPropertyNode("festivalCode1").addConstraintViolation();
			isValid = false;
		}

		// Check divisible by 3
		if (code2 <= 0 || code2 % 3 != 0) {
			context.buildConstraintViolationWithTemplate(
					"FestivalCode2 moet een positief getal zijn dat deelbaar is door 3.")
					.addPropertyNode("festivalCode2").addConstraintViolation();
			isValid = false;
		}

		// Check difference < 300
		if (Math.abs(code1 - code2) >= 300) {
			context.buildConstraintViolationWithTemplate(
					"Het verschil tussen FestivalCode1 en FestivalCode2 moet kleiner zijn dan 300.")
					.addConstraintViolation();
			isValid = false;
		}

		return isValid;
	}
}
