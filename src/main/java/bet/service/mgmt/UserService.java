package bet.service.mgmt;

import bet.api.dto.BetDto;
import bet.api.dto.GameDto;
import bet.api.dto.OddDto;
import bet.api.dto.UserDto;
import bet.model.Bet;
import bet.model.Game;
import bet.model.Odd;
import bet.model.User;
import bet.repository.UserRepository;
import bet.service.email.EmailSender;
import bet.service.utils.EncryptUtils;
import bet.service.utils.PasswordGenerator;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
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
		String password = passwordGenerator.generatePassword();
		dto.setPassword(hashPassword(dto.getName(), password));
		dto.setRole("USER");
		dto = super.create(dto);
		sendPasswordEmail(dto, password);
		return dto;
	}

	private String hashPassword(String username, String password) {
		return passwordEncoder.encode(password);
//		try {
//			return encryptUtils.encrypt(password, username );
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
		//		MessageDigest md = null;
//		try {
//			md = MessageDigest.getInstance("MD5");
//		} catch (NoSuchAlgorithmException e) {
//			throw new RuntimeException(e);
//		}
//		return new String(Base64.getEncoder().encode(md.digest(password.getBytes())));
	}

	private void sendPasswordEmail(UserDto dto, String password) {
		String body = String.format("<html><body>Username:%s<br/>Password:%s</body></html>", dto.getName(), password);
		emailSender.sendEmail(dto.getEmail(), "Upstream WC2018 account", body);
	}

	@Override
	public UserDto update(UserDto dto) {
		String password = dto.getPassword();
		dto.setPassword(hashPassword(dto.getName(), password));
		dto.setRole("USER");
		dto = super.create(dto);
		sendPasswordEmail(dto, password);
		return dto;
	}


}
