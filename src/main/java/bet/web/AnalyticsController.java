package bet.web;

import bet.model.RankHistory;
import bet.model.UserStreak;
import bet.model.UserStreakHistory;
import bet.repository.*;
import bet.service.analytics.AnalyticsScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.util.*;
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
    private CustomBetRepository customBetRepository;

    @Autowired
    private RankHistoryRepository rankHistoryRepository;

    @Autowired
    private UserStreakRepository userStreakRepository;

    @Autowired
    private UserStreakHistoryRepository userStreakHistoryRepository;

    @Autowired
    private AnalyticsScheduler analyticsScheduler;

    @Autowired
    private OddRepository oodOddRepository;

    @Value("${application.timezone}")
    private String timezone;

    /**
     * Get a sorted list of username->risk index for each user
     * @return
     */
    @Cacheable(value = "userBets2")
    @RequestMapping(value = "/riskIndex", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Map<String, String>> riskIndex() {
        Map<String, Double> riskIndex = customBetRepository.listRiskIndex();
        return riskIndex.entrySet().stream().sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .map(e -> new HashMap<String, String>() {{
                    put("username", e.getKey());
                    put("riskIndex", String.format("%.2f", e.getValue()));
                }}).collect(Collectors.toList());
    }

    /**
     * Get the ranking history sorted by date
     * @param userName
     * @return
     */
    @Cacheable(value = "analytics1")
    @RequestMapping(value = "/rankHistory", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<RankHistory> rankHistory(@RequestParam(value = "userName", required = false) String userName) {

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
     */
    @Cacheable(value = "analytics2")
    @RequestMapping(value = "/topRanked", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, Long> topRanked() {
        return StreamSupport.stream(rankHistoryRepository.findAll().spliterator(), false)
                .filter(rankHistory -> rankHistory.getRank() == 1)
                .collect(Collectors.groupingBy(o ->  o.getUser().getUsername(), Collectors.counting()));

    }

    /**
     * Get the user streak sorted by streak (desc)
     * @return
     */
    @Cacheable(value = "analytics3")
    @RequestMapping(value = "/userStreak", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<UserStreak> userStreak() {
        return StreamSupport.stream(userStreakRepository.findAll().spliterator(), false)
                .sorted((o1, o2) -> {
                    return o2.getStreak().compareTo(o1.getStreak());
                })
                .collect(Collectors.toList());
    }

    /**
     * Get the user streak history  sorted by streak (desc)
     * @return
     */
    @Cacheable(value = "analytics4")
    @RequestMapping(value = "/userStreakHistory", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<UserStreakHistory> userStreakHistory(@RequestParam(value = "sortByMax", required = false, defaultValue = "true") boolean sortByMax) {
        return StreamSupport.stream(userStreakHistoryRepository.findAll().spliterator(), false)
                .sorted((o1, o2) -> {
                    if(sortByMax) {
                        return o2.getMaxStreak().compareTo(o1.getMaxStreak());
                    } else {
                        return o2.getMinStreak().compareTo(o1.getMinStreak());
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Get the min max user streak history
     *
     * @return
     */
    @Cacheable(value = "analytics5")
    @RequestMapping(value = "/userStreakHistoryLimits", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, UserStreakHistory> userStreakHistoryLimits() {
        UserStreakHistory minStreak = StreamSupport.stream(userStreakHistoryRepository.findAll().spliterator(), false)
                .min(Comparator.comparing(UserStreakHistory::getMinStreak)).get();
        UserStreakHistory maxStreak = StreamSupport.stream(userStreakHistoryRepository.findAll().spliterator(), false)
                .max(Comparator.comparing(UserStreakHistory::getMaxStreak)).get();
        return new HashMap<String, UserStreakHistory>() {{
            put("max", maxStreak);
            put("min", minStreak);
        }};
    }


    /**
     * Get the min max user streak history
     *
     * @return
     */
    @Cacheable(value = "analytics6")
    @RequestMapping(value = "/riskIndexMax", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Double riskIndexMax() {
        return StreamSupport.stream(oodOddRepository.findAll().spliterator(), false)
                .map(odd -> (double) Collections.max(Arrays.asList(odd.getOddsHome(), odd.getOddsAway(), odd.getOddsTie())) +
                        (double) Collections.max(Arrays.asList(odd.getOddsOver(), odd.getOddsUnder()))
                ).collect(Collectors.summingDouble(value -> value));
    }


    /**
     * Get last run date for reporting
     * @return
     */
    @RequestMapping(value = "/lastupdate", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String liveFeedLastUpdate() {
        RankHistory maxDate = rankHistoryRepository.findFirstByOrderByRankDateDesc();
        if (maxDate != null) {
            return maxDate.getRankDate().withZoneSameInstant(ZoneId.of(timezone)).toString();
        }
        return "N/A";
    }


}
