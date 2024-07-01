package bet.service.mgmt;

import bet.api.dto.UserDto;
import bet.exception.UserAlreadyExistException;
import bet.model.User;
import bet.service.email.EmailSender;
import bet.service.encrypt.EncryptHelper;
import bet.service.utils.PasswordGenerator;
import com.google.common.collect.Lists;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
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
	@Cacheable(value = "users1")
	public List<UserDto> list() {
		return list(null);
	}

	@Cacheable(value = "users2")
	public List<UserDto> list(String username) {
		return Lists.newArrayList(repository.findAll()).stream()
				.filter(user -> username == null || user.getUsername().equals(username))
				.map(entity -> {
					UserDto dto = new UserDto();
					dto.fromEntity(entity);
					try {
						dto.setName(encryptHelper.decrypt(dto.getName(), RANDOM_SALT));
						dto.setEmail(encryptHelper.decrypt(dto.getEmail(), RANDOM_SALT));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					return dto;
				}).collect(Collectors.toList());
	}

	@Override
	@CacheEvict(allEntries = true, cacheNames = {"users1","users2"})
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
		//hash provided email
		String email = dto.getEmail();
		if(email != null) {
			try {
				dto.setEmail(encryptHelper.encrypt(dto.getEmail(), RANDOM_SALT));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		//all users are created with role USER
		dto.setRole("USER");
		try {
			dto = super.create(dto);
		} catch (DataIntegrityViolationException e) {
			Throwable cause = e.getCause();
			if (cause instanceof ConstraintViolationException) {
				ConstraintViolationException constraintViolationException = (ConstraintViolationException) cause;
				String constraintName = constraintViolationException.getConstraintName();
				if ("allowed_users_u01".equals(constraintName) ||
						"allowed_users_u02".equals(constraintName) ||
						"allowed_users_u03".equals(constraintName)) {
					throw new UserAlreadyExistException();
				} else {
					throw new RuntimeException("An unexpected error occurred while creating user");
				}
			} else {
				throw new RuntimeException("An unexpected error occurred while creating user");
			}
		}

		//send email to user to provide the password
		sendPasswordEmail(dto, password, email);
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
	private void sendPasswordEmail(UserDto dto, String password, String email) {
		Context context = new Context();
		context.setVariable("username", dto.getUsername());
		context.setVariable("password", password);
		String html = templateEngine.process("email-user", context);

		emailSender.sendEmail(email, "Euro 2024 Challenge account", html, false);
	}

}
