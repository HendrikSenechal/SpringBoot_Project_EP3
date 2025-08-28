package constants;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class PeriodConstants {

	private PeriodConstants() {
	}

	public static final int CURRENT_YEAR = LocalDate.now().getYear();

	public static final LocalDateTime PERIOD_START = LocalDate.of(CURRENT_YEAR, 6, 1).atStartOfDay();

	public static final LocalDateTime PERIOD_END = LocalDate.of(CURRENT_YEAR, 9, 30).atTime(23, 59, 59);
}
