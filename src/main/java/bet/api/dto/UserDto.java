package bet.api.dto;

import bet.model.User;
import lombok.Data;

/**
 * The app user
 */
@Data
public class UserDto implements ManagementDto<User, Integer> {

	private static final long serialVersionUID = 1592379971444078279L;

	private int id;

	private String username;

	private String name;

	private String email;

	private String password;

	private String role;

	private Boolean optOut;

	private Boolean eligible;

	public UserDto() {
	}

	public UserDto(String name, String email, String password, String role, String username, Boolean optOut) {
		this(name, email, password, role, username, optOut, true);
	}

	public UserDto(String name, String email, String password, String role, String username, Boolean optOut, Boolean eligible) {
		this.name = name;
		this.username = username;
		this.email = email;
		this.password = password;
		this.role = role;
		this.optOut = optOut;
		this.eligible = eligible;

	}

	@Override
	public void fromEntity(User entity) {
		if (entity != null) {
			setId(entity.getId());
			setName(entity.getName());
			setUsername(entity.getUsername());
			setEmail(entity.getEmail());
			setPassword(entity.getPassword());
			setRole(entity.getRole());
			setOptOut(entity.getOptOut());
			setEligible(entity.getEligible());
		}
	}

	@Override
	public User toEntity() {
		return new User(this.id, this.name, this.email, this.password, this.role, this.username, this.optOut, this.eligible);
	}

}
