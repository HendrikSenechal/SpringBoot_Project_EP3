package validation;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import entity.Vendor;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoDuplicateVendorsValidator implements ConstraintValidator<NoDuplicateVendors, Set<Vendor>> {

	@Override
	public boolean isValid(Set<Vendor> value, ConstraintValidatorContext context) {
		if (value == null || value.isEmpty())
			return true;

		Set<Object> seen = new HashSet<>();
		boolean ok = value.stream().allMatch(v -> seen.add(vKey(v)));

		if (!ok) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Dubbele standhouder gedetecteerd.").addConstraintViolation();
		}
		return ok;
	}

	private Object vKey(Vendor v) {
		if (v == null)
			return new Object();
		return v.getId() != null ? v.getId() : Objects.hash(v.getName());
	}
}
