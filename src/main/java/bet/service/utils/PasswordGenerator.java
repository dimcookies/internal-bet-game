package bet.service.utils;

import bet.repository.GameRepository;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class PasswordGenerator {
	public String generatePassword() {
		return RandomStringUtils.random(8, 0, 20, true, true, "1234567890qazxswedcvfrtgbnhyujmkiolp".toCharArray()).toLowerCase();
	}
}