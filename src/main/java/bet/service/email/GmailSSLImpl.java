package bet.service.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Slf4j
@Component
@Profile("live")
public class GmailSSLImpl implements EmailSender {

	@Value("${application.email.username}")
	private String username;

	@Value("${application.email.password}")
	private String password;

	public void sendEmail(String emailTo, String subject, String body, boolean cc) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(emailTo));
			if(cc) {
				message.setRecipients(Message.RecipientType.CC,
						InternetAddress.parse(username));
			}
			message.setSubject(subject);
			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

		} catch (MessagingException e) {
			//throw new RuntimeException(e);
			log.error("Error sending mail: " + emailTo, e);
		}
	}
}
