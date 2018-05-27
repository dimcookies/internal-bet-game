package bet.web.mgmt;

import bet.api.dto.BetDto;
import bet.model.Bet;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Management-api")
@RestController
@RequestMapping("/config/bets")
public class BetManagementController extends AbstractBetManagementController<BetDto, Integer, Bet> {

}
