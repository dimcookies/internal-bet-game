package bet.web;

import bet.model.RankHistory;
import bet.model.UserStreak;
import bet.model.UserStreakHistory;
import bet.repository.BetRepository;
import bet.repository.RankHistoryRepository;
import bet.repository.UserStreakHistoryRepository;
import bet.repository.UserStreakRepository;
import bet.service.analytics.Analytics;
import bet.service.analytics.AnalyticsScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
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

/**
 * Web services for reporting
 */
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
    private UserStreakHistoryRepository userStreakHistoryRepository;

    @Autowired
    private AnalyticsScheduler analyticsScheduler;

    @Value("${application.timezone}")
    private String timezone;

    /**
     * Get a sorted list of username->risk index for each user
     * @return
     * @throws Exception
     */
    @Cacheable(value = "userBets2")
    @RequestMapping(value = "/riskIndex", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Map<String, String>> riskIndex() throws Exception {
        Map<String, Double> riskIndex = betRepository.listRiskIndex();
        return riskIndex.entrySet().stream().sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .map(e -> new HashMap<String, String>() {{
                    put("username", e.getKey());
                    put("riskIndex", e.getValue().toString());
                }}).collect(Collectors.toList());
    }

    /**
     * Get the ranking history sorted by date
     * @param userName
     * @return
     * @throws Exception
     */
    @Cacheable(value = "analytics1")
    @RequestMapping(value = "/rankHistory", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<RankHistory> rankHistory(@RequestParam(value = "userName", required = false) String userName) throws Exception {

        return StreamSupport.stream(rankHistoryRepository.findAll().spliterator(), false)
                .filter(rankHistory -> userName == null || rankHistory.getUser().getUsername().equals(userName))
                .sorted((o1, o2) -> {
                    //sort by date and then by rank
                    int cmp1 = o1.getRankDate().compareTo(o2.getRankDate());
                    if(cmp1 != 0) {
                        return  cmp1;
                    }
                    return o2.getRank().compareTo(o1.getRank());
                })
                .collect(Collectors.toList());
    }

    /**
     * Get a map username-> #times ranked as number one
     * @return
     * @throws Exception
     */
    @Cacheable(value = "analytics2")
    @RequestMapping(value = "/topRanked", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, Long> topRanked() throws Exception {
        return StreamSupport.stream(rankHistoryRepository.findAll().spliterator(), false)
                .filter(rankHistory -> rankHistory.getRank() == 1)
                .collect(Collectors.groupingBy(o ->  o.getUser().getUsername(), Collectors.counting()));

    }

    /**
     * Get the user streak sorted by streak (desc)
     * @return
     * @throws Exception
     */
    @Cacheable(value = "analytics3")
    @RequestMapping(value = "/userStreak", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<UserStreak> userStreak() throws Exception {
        return StreamSupport.stream(userStreakRepository.findAll().spliterator(), false)
                .sorted((o1, o2) -> {
                    return o2.getStreak().compareTo(o1.getStreak());
                })
                .collect(Collectors.toList());
    }

    /**
     * Get the user streak history  sorted by streak (desc)
     * @return
     * @throws Exception
     */
    @Cacheable(value = "analytics4")
    @RequestMapping(value = "/userStreakHistory", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<UserStreakHistory> userStreakHistory(@RequestParam(value = "sortByMax", required = false, defaultValue = "true") boolean sortByMax) throws Exception {
        return StreamSupport.stream(userStreakHistoryRepository.findAll().spliterator(), false)
                .sorted((o1, o2) -> {
                    if(sortByMax) {
                        return o2.getMaxStreak().compareTo(o1.getMaxStreak());
                    } else {
                        return o1.getMinStreak().compareTo(o1.getMinStreak());
                    }
                })
                .collect(Collectors.toList());
    }


    /**
     * Get last run date for reporting
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/lastupdate", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String liveFeedLastUpdate() throws Exception {
        ZonedDateTime lastUpdate = analyticsScheduler.getLastUpdateDate();
        return lastUpdate != null ? lastUpdate.withZoneSameInstant(ZoneId.of(timezone)).toString() : "N/A";
    }


}
