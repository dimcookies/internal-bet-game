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

import java.security.Principal;

@Api(value = "Management-api")
@RestController
@RequestMapping("/config/encryptedbets")
public class EncryptedBetController extends AbstractBetController<EncryptedBetDto, Integer, EncryptedBet> {

	@Autowired
	private EncryptedBetService encryptedBetService;

	@Autowired
	private UserRepository userRepository;

	@RequestMapping(value = "/decryptandmove", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public String decryptandmove() throws Exception {
		encryptedBetService.decryptAndCopy();
		return "Ok";
	}

	@RequestMapping(path = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public EncryptedBetDto create(@RequestBody EncryptedBetDto model) {
		throw new RuntimeException();
	}

	@RequestMapping(path = "/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public EncryptedBetDto update(@RequestBody EncryptedBetDto model) {
		throw new RuntimeException();
	}

	@RequestMapping(path = "/create2", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public EncryptedBetDto create2(@RequestBody EncryptedBetDto model, Principal principal) {
		User user = userRepository.findOneByName(principal.getName());
		model.setUserId(user.getId());
		return service.create(model);
	}

	@RequestMapping(path = "/update2", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public EncryptedBetDto update2(@RequestBody EncryptedBetDto model, Principal principal) {
		User user = userRepository.findOneByName(principal.getName());
		model.setUserId(user.getId());
		return service.update(model);
	}
}
