package bet.web.mgmt;

import bet.api.dto.BetDto;
import bet.api.dto.UserDto;
import bet.model.Bet;
import bet.model.User;
import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Management-api")
@RestController
@RequestMapping("/config/users")
public class UserManagementController extends AbstractBetManagementController<UserDto, Integer, User> {
	@RequestMapping(path = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserDto create(@RequestBody UserDto model) {
		return service.create(model);
	}

	@RequestMapping(path = "/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserDto update(@RequestBody UserDto model) {
		return service.update(model);
	}
}
