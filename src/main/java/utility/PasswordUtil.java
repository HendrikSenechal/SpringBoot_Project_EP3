package utility;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for password hashing and salt generation using PBKDF2 with HMAC
 * SHA-256.
 */
public class PasswordUtil {

	private static final Logger log = LoggerFactory.getLogger(PasswordUtil.class);

	/** Number of iterations for the key derivation function. */
	private static final int ITERATIONS = 100_000;

	/** Length of the generated key in bits. */
	private static final int KEY_LENGTH = 256;

	/**
	 * Hashes a password with the given salt using PBKDF2 with HMAC SHA-256.
	 *
	 * @param password the plain text password to hash
	 * @param salt     the salt to use in hashing (should be securely generated and
	 *                 unique per password)
	 * @return the hashed password encoded as a Base64 string
	 * @throws RuntimeException if the hashing algorithm is not available or any
	 *                          other cryptographic error occurs
	 */
	public static String hashPassword(String password, byte[] salt) {
		// log.info("Starting password hashing");
		try {
			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			byte[] hash = skf.generateSecret(spec).getEncoded();
			// log.info("Password hashed successfully");
			return Base64.getEncoder().encodeToString(hash);
		} catch (Exception e) {
			log.error("Error while hashing password", e);
			throw new RuntimeException("Error while hashing password", e);
		}
	}

	/**
	 * Generates a new cryptographically secure random salt.
	 *
	 * @return a byte array containing the generated salt (16 bytes long)
	 */
	public static byte[] generateSalt() {
		// log.info("Generating new salt");
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		// log.info("Salt generated successfully");
		return salt;
	}
}
