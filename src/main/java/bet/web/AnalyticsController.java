package bet.web;

import bet.model.RankHistory;
import bet.model.UserStreak;
import bet.repository.BetRepository;
import bet.repository.RankHistoryRepository;
import bet.repository.UserStreakRepository;
import bet.service.analytics.Analytics;
import bet.service.analytics.AnalyticsScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private RankHistoryRepository rankHistoryRepository;

    @Autowired
    private UserStreakRepository userStreakRepository;

    @Autowired
    private AnalyticsScheduler analyticsScheduler;

    @Value("${application.timezone}")
    private String timezone;

    @RequestMapping(value = "/riskIndex", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Map<String, String>> riskIndex() throws Exception {
        Map<String, Double> allPoints = betRepository.listRiskIndex();
        return allPoints.entrySet().stream().sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .map(e -> new HashMap<String, String>() {{
                    put("username", e.getKey());
                    put("riskIndex", e.getValue().toString());
                }}).collect(Collectors.toList());
    }

    @RequestMapping(value = "/rankHistory", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<RankHistory> rankHistory(@RequestParam(value = "userName", required = false) String userName) throws Exception {

        return StreamSupport.stream(rankHistoryRepository.findAll().spliterator(), false)
                .filter(rankHistory -> userName == null || rankHistory.getUser().getName().equals(userName))
                .sorted((o1, o2) -> {
                    int cmp1 = o1.getRankDate().compareTo(o2.getRankDate());
                    if(cmp1 != 0) {
                        return  cmp1;
                    }
                    return o2.getRank().compareTo(o1.getRank());
                })
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/topRanked", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, Long> topRanked() throws Exception {
        return StreamSupport.stream(rankHistoryRepository.findAll().spliterator(), false)
                .filter(rankHistory -> rankHistory.getRank() == 1)
                .collect(Collectors.groupingBy(o ->  o.getUser().getName(), Collectors.counting()));

    }

    @RequestMapping(value = "/userStreak", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<UserStreak> userStreak() throws Exception {
        return StreamSupport.stream(userStreakRepository.findAll().spliterator(), false)
                .sorted((o1, o2) -> {
                    return o2.getStreak().compareTo(o1.getStreak());
                })
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/lastupdate", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String liveFeedLastUpdate() throws Exception {
        ZonedDateTime lastUpdate = analyticsScheduler.getLastUpdateDate();
        return lastUpdate != null ? lastUpdate.withZoneSameInstant(ZoneId.of(timezone)).toString() : "N/A";
    }


}