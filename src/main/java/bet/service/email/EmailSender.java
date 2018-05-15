package bet.service.email;

public interface EmailSender {
	void sendEmail(String emailTo, String subject, String body);
}
