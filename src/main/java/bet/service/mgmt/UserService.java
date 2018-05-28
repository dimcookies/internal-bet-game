package bet.service.mgmt;

import bet.api.dto.UserDto;
import bet.model.User;
import bet.service.email.EmailSender;
import bet.service.utils.PasswordGenerator;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService extends AbstractManagementService<User, Integer, UserDto> {

	@Autowired
	private PasswordGenerator passwordGenerator;

	@Autowired
	private EmailSender emailSender;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public List<UserDto> list() {
		return Lists.newArrayList(repository.findAll()).stream().map(entity -> {
			UserDto dto = new UserDto();
			dto.fromEntity(entity);
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public UserDto create(UserDto dto) {
		//generate a random password
		String password = passwordGenerator.generatePassword();
		//hash provided password
		dto.setPassword(hashPassword(dto.getName(), password));
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
		String body = String.format("<html><body>Username:%s<br/>Password:%s</body></html>", dto.getName(), password);
		emailSender.sendEmail(dto.getEmail(), "Upstream WC2018 account", body);
	}

	@Override
	public UserDto update(UserDto dto) {
		String password = dto.getPassword();
		//update password
		dto.setPassword(hashPassword(dto.getName(), password));
		dto.setRole("USER");
		dto = super.create(dto);
		sendPasswordEmail(dto, password);
		return dto;
	}


}
