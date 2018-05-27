package bet.web.mgmt;

import bet.api.dto.OddDto;
import bet.model.Odd;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Management-api")
@RestController
@RequestMapping("/config/odd")
public class OddManagementController extends AbstractBetManagementController<OddDto, Integer, Odd> {

}
