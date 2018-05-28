package bet.web.mgmt;

import bet.api.dto.BetDto;
import bet.api.dto.EncryptedBetDto;
import bet.model.Bet;
import bet.model.EncryptedBet;
import bet.model.User;
import bet.repository.UserRepository;
import bet.service.mgmt.EncryptedBetService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.security.Principal;
import java.util.List;

@Api(value = "Management-api")
@RestController
@RequestMapping("/config/encryptedbets")
public class EncryptedBetManagementController extends AbstractBetManagementController<EncryptedBetDto, Integer, EncryptedBet> {

	//do not allow creation of bets, they are copied from encrypted
	@RequestMapping(path = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public EncryptedBetDto create(@RequestBody EncryptedBetDto model) {
		throw new NotImplementedException();
	}

	//do not allow update of bets, they are copied from encrypted
	@RequestMapping(path = "/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public EncryptedBetDto update(@RequestBody EncryptedBetDto model) {
		throw new NotImplementedException();
	}


}
