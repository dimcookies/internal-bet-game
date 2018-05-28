package bet.service.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

/**
 * Generate a random password with 8 characters from digits and letters
 */
@Component
public class PasswordGenerator {
	public String generatePassword() {
		return RandomStringUtils.random(8, 0, 20, true, true, "1234567890qazxswedcvfrtgbnhyujmkiolp".toCharArray()).toLowerCase();
	}
}