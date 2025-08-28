package validation;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import constants.PeriodConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class WithinFestivalPeriodValidator implements ConstraintValidator<WithinFestivalPeriod, Object> {

	private String startFieldName;
	private String endFieldName;

	private static final LocalDateTime PERIOD_START = PeriodConstants.PERIOD_START;
	private static final LocalDateTime PERIOD_END = PeriodConstants.PERIOD_END;

	@Override
	public void initialize(WithinFestivalPeriod ann) {
		this.startFieldName = ann.startField();
		this.endFieldName = ann.endField();
	}

	@Override
	public boolean isValid(Object bean, ConstraintValidatorContext ctx) {
		if (bean == null)
			return true;

		try {
			LocalDateTime start = (LocalDateTime) read(bean, startFieldName);
			LocalDateTime end = (LocalDateTime) read(bean, endFieldName);

			// @NotNull op velden behandelt nulls
			if (start == null || end == null)
				return true;

			boolean ok = !start.isBefore(PERIOD_START) && !end.isAfter(PERIOD_END) && end.isAfter(start);

			if (!ok) {
				ctx.disableDefaultConstraintViolation();
				ctx.buildConstraintViolationWithTemplate(
						"Datums moeten liggen tussen %s en %s en 'end' moet na 'start' zijn.".formatted(PERIOD_START,
								PERIOD_END))
						.addPropertyNode(endFieldName).addConstraintViolation();
			}
			return ok;
		} catch (ReflectiveOperationException | ClassCastException e) {
			return false; // kan veld niet lezen of type mismatch
		}
	}

	private Object read(Object target, String field) throws ReflectiveOperationException {
		Field f = target.getClass().getDeclaredField(field);
		f.setAccessible(true);
		return f.get(target);
	}
}
