package bet.service.mgmt;

import bet.api.dto.UserDto;
import bet.model.User;
import bet.service.email.EmailSender;
import bet.service.encrypt.EncryptHelper;
import bet.service.utils.PasswordGenerator;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService extends AbstractManagementService<User, Integer, UserDto> {

	private static final String RANDOM_SALT = "NM_SALT";
	@Autowired
	private PasswordGenerator passwordGenerator;

	@Autowired
	private EmailSender emailSender;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EncryptHelper encryptHelper;

	@Autowired
	private SpringTemplateEngine templateEngine;

	@Override
	public List<UserDto> list() {
		return Lists.newArrayList(repository.findAll()).stream().map(entity -> {
			UserDto dto = new UserDto();
			dto.fromEntity(entity);
			try {
				dto.setName(encryptHelper.decrypt(dto.getName(), RANDOM_SALT));
			} catch (Exception e) {
				throw new RuntimeException();
			}
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public UserDto create(UserDto dto) {
		//generate a random password
		String password = passwordGenerator.generatePassword();
		//hash provided password
		dto.setPassword(hashPassword(dto.getName(), password));
		if(dto.getName() != null) {
			try {
				dto.setName(encryptHelper.encrypt(dto.getName(), RANDOM_SALT));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		//all users are created with role USER
		dto.setRole("USER");
		dto = super.create(dto);
		//send email to user to provide the password
		sendPasswordEmail(dto, password);
		return dto;
	}

	private String hashPassword(String username, String password) {
		return passwordEncoder.encode(password);
	}

	/**
	 * Send an email to user that contains the password
	 * @param dto
	 * @param password
	 */
	private void sendPasswordEmail(UserDto dto, String password) {
		Context context = new Context();
		context.setVariable("username", dto.getUsername());
		context.setVariable("password", password);
		String html = templateEngine.process("email-user", context);

		emailSender.sendEmail(dto.getEmail(), "Upstream WC2018 account", html);
	}

}
