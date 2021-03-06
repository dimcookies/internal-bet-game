package bet.web.mgmt;

import bet.api.dto.DeadlineDto;
import bet.model.Deadline;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Management-api")
@RestController
@RequestMapping("/config/deadlines")
public class DeadlineManagementController extends AbstractBetManagementController<DeadlineDto, Integer, Deadline> {

}
