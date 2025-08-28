package utils;

import java.security.SecureRandom;

public class FestivalCodeGenerator {

	private static final SecureRandom RANDOM = new SecureRandom();

	public static int[] generateValidCodes() {
		int code1;
		int code2;

		do {
			code1 = (RANDOM.nextInt(4999) + 1) * 2;

			code2 = (RANDOM.nextInt(3333) + 1) * 3;

		} while (Math.abs(code1 - code2) >= 300);

		return new int[] { code1, code2 };
	}
}
