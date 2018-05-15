package bet.api.dto;

import bet.api.constants.OverResult;
import bet.api.constants.ScoreResult;
import bet.model.Bet;
import bet.model.Game;
import bet.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class UserDto implements ManagementDto<User, Integer> {

	private static final long serialVersionUID = 1592379971444078279L;

	private int id;

	private String name;

	private String email;

	private String password;

	private String role;

	public UserDto() {
	}

	public UserDto(String name, String email, String password, String role) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.role = role;
	}

	@Override
	public void fromEntity(User entity) {
		if (entity != null) {
			setId(entity.getId());
			setName(entity.getName());
			setEmail(entity.getEmail());
			setPassword(entity.getPassword());
			setRole(entity.getRole());
		}
	}

	@Override
	public User toEntity() {
		return new User(this.id, this.name, this.email, this.password, this.role);
	}

}
