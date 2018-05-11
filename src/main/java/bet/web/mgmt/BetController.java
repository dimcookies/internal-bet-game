package bet.web.mgmt;

import bet.api.dto.BetDto;
import bet.api.dto.GameDto;
import bet.model.Bet;
import bet.model.Game;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Management-api")
@RestController
@RequestMapping("/config/bets")
public class BetController extends AbstractOodController<BetDto, Integer, Bet> {

}
