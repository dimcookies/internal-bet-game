package bet.web;

import bet.api.dto.EncryptedBetDto;
import bet.model.Bet;
import bet.model.User;
import bet.repository.BetRepository;
import bet.repository.UserRepository;
import bet.service.mgmt.EncryptedBetService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bets/")
public class BetsController {

    @Autowired
    private EncryptedBetService encryptedBetService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BetRepository betRepository;

    @Value("${application.allowedMatchDays}")
    private String allowedMatchDays;

    @RequestMapping(path = "/encrypted/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EncryptedBetDto> create(@RequestBody List<EncryptedBetDto> bets, Principal principal) {
        User user = userRepository.findOneByName(principal.getName());
        return encryptedBetService.createAll(bets, user);
    }

    @RequestMapping(path = "/encrypted/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EncryptedBetDto> createAllBets(Principal principal) {
        User user = userRepository.findOneByName(principal.getName());
        return encryptedBetService.list(user);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Bet> allBets(@RequestParam(value = "userId", required = false) Integer userId,
                             @RequestParam(value = "userName", required = false) String userName,
                             @RequestParam(value = "gameId", required = false) Integer gameId) throws Exception {
        return Lists.newArrayList(betRepository.findAll()).stream()
                .filter(bet -> userId == null || bet.getUser().getId().equals(userId))
                .filter(bet -> userName == null || bet.getUser().getName().equals(userName))
                .filter(bet -> gameId == null || bet.getGame().getId().equals(gameId))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/points", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Map<String, String>> allPoints() throws Exception {
        Map<String, Integer> allPoints = betRepository.listAllPoints();
        return allPoints.entrySet().stream().sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .map(e -> new HashMap<String, String>() {{
                    put("username", e.getKey());
                    put("points", e.getValue().toString());
                }}).collect(Collectors.toList());
    }

    @RequestMapping(value = "/allowedMatchDays", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String allowedMatchDays() throws Exception {
        return allowedMatchDays;
    }

}