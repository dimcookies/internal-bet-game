package bet.service.mgmt;

import bet.api.dto.UserDto;
import bet.model.User;
import bet.service.email.EmailSender;
import bet.service.utils.AESEncryptHelper;
import bet.service.utils.EncryptHelper;
import bet.service.utils.PasswordGenerator;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
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
		String body = String.format("<html><body>Username:%s<br/>Password:%s</body></html>", dto.getName(), password);
		emailSender.sendEmail(dto.getEmail(), "Upstream WC2018 account", body);
	}

	@Override
	public UserDto update(UserDto dto) {
		String password = dto.getPassword();
		if(password != null) {
			//update password
			dto.setPassword(hashPassword(dto.getName(), password));
		}
        if(dto.getName() != null) {
            try {
                dto.setName(encryptHelper.encrypt(dto.getName(), RANDOM_SALT));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
		dto.setRole("USER");
		dto = super.create(dto);
		sendPasswordEmail(dto, password);
		return dto;
	}

}
