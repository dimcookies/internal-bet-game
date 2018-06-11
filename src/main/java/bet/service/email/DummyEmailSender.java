package bet.service.email;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DummyEmailSender implements EmailSender {


	public void sendEmail(String emailTo, String subject, String body, boolean cc) {

	}
}
