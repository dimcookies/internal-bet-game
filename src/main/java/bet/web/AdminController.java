package bet.web;

import bet.api.dto.EncryptedBetDto;
import bet.api.dto.GameDto;
import bet.api.dto.UserDto;
import bet.model.Game;
import bet.model.Odd;
import bet.model.User;
import bet.repository.DeadlineRepository;
import bet.repository.GameRepository;
import bet.repository.OddRepository;
import bet.repository.UserRepository;
import bet.service.analytics.AnalyticsScheduler;
import bet.service.email.EmailSender;
import bet.service.livefeed.LiveScoreFeedScheduler;
import bet.service.mgmt.EncryptedBetService;
import bet.service.mgmt.UserService;
import bet.service.rss.RssFeedScheduler;
import bet.service.utils.EhCacheUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Helper services for administration tasks
 */
@Slf4j
@RestController
@RequestMapping("/config")
public class AdminController {

    @Autowired
    private LiveScoreFeedScheduler liveScoreFeedScheduler;

    @Autowired
    private EncryptedBetService encryptedBetService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private AnalyticsScheduler analyticsScheduler;

    @Autowired(required = false)
    private RssFeedScheduler rssFeedScheduler;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private OddRepository oddRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private DeadlineRepository deadlineRepository;

    @Autowired
    private EmailSender emailSender;

    /**
     * Manually perform a score update from the live feed
     *
     * @return
     */
    @CacheEvict(allEntries = true, cacheNames = {"points1","points2","games"})
    @RequestMapping(value = "/liveupdate", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public String liveupdate() {
        liveScoreFeedScheduler.getLiveScores(false);
        return "OK";
    }

    /**
     * Manually update a game
     * @param gameDto
     * @return
     */
    @CacheEvict(allEntries = true, cacheNames = {"points1","points2","games"})
    @RequestMapping(value = "/manualScoreUpdate", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public String manualScoreUpdate(@RequestBody GameDto gameDto) {
        liveScoreFeedScheduler.checkMatchChanged(gameDto, true);
        liveScoreFeedScheduler.setLastUpdateDate(ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")));
        return "OK";
    }

    /**
     * Move encrypted bets to public available
     *
     * @return
     */
    @RequestMapping(value = "/decryptandmove", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String decryptandmove() {
        encryptedBetService.decryptAndCopy();
        return "Ok";
    }


    /**
     * Change user password
     *
     * @return
     */
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public String changePassword(String password, String username) {
        User user = userRepository.findOneByUsername(username);
        UserDto userDto = new UserDto();
        userDto.fromEntity(user);
        if (password != null) {
            userDto.setPassword(passwordEncoder.encode(password));
        }
        userService.update(userDto);
        return "OK";
    }

    /**
     * Toggle user eligibility
     *
     * @return
     */
    @RequestMapping(value = "/toggleEligibleUser", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public String toggleEligibleUser(String username) {
        User user = userRepository.findOneByUsername(username);
        UserDto userDto = new UserDto();
        userDto.fromEntity(user);
        userDto.setEligible(!user.getEligible());
        userService.update(userDto);
        return "OK";
    }
    /**
     * Manual run analytics
     *
     * @return
     */
    @RequestMapping(value = "/manualAnalytics", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public String manualAnalytics() {
        analyticsScheduler.runAnalytics();
        return "OK";
    }

    /**
     * Manual run rss
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/manualRss", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public String manualRss() throws Exception {
        if(rssFeedScheduler != null) {
            rssFeedScheduler.getRssFeed();
        }
        return "OK";
    }

    /**
     * Manual run rss
     *
     * @return
     */
    @RequestMapping(value = "/clearCache", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public String manualClearCaches() {
        cacheManager.getCacheNames().parallelStream().forEach(name -> cacheManager.getCache(name).clear());
        EhCacheUtils.clearCache();
        return "OK";
    }


    @RequestMapping(value = "/uploadOdds", method = RequestMethod.POST)
    public String submit(@RequestParam("file") MultipartFile file) {
        try {
            new BufferedReader(new InputStreamReader(file.getInputStream())).lines()
                    .forEach(s -> {
                        if(!NumberUtils.isNumber(s.substring(0,1))) {
                            log.info("Ignoring header line:" + s);
                            return;
                        }
                        String[] values = s.split(",");
                        Game game = gameRepository.findOne(Integer.parseInt(values[0]));
                        if(game != null) {
                            Odd odd = oddRepository.findOneByGame(game);
                            if (odd != null) {
                                odd.setOddsHome(Float.parseFloat(values[4]));
                                odd.setOddsTie(Float.parseFloat(values[5]));
                                odd.setOddsAway(Float.parseFloat(values[6]));
                                if(values.length > 7) {
                                    odd.setOddsUnder(Float.parseFloat(values[7]));
                                    odd.setOddsOver(Float.parseFloat(values[8]));
                                }
                                oddRepository.save(odd);
                                log.info("Odd updated:" + odd);
                            } else {
                                throw new RuntimeException("Odd not found for:" + s);
                            }
                        } else {
                            throw new RuntimeException("Game not found for:" + s);
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        manualClearCaches();
        return "OK";
    }

    @RequestMapping(value = "/uploadUsers", method = RequestMethod.POST)
    public Map<String, String> uploadUsers(@RequestParam("file") MultipartFile file) {
        Map<String, String> res = new HashMap<>();
        try {
            new BufferedReader(new InputStreamReader(file.getInputStream())).lines()
                    .forEach(s -> {
                        if(StringUtils.isBlank(s)) {
                            return;
                        }
                        String[] values = s.split(",");
                        if(values.length != 2) {
                            res.put(s, "ERROR");
                            return;
                        }
                        String name = values[0];
                        String email = values[1];
                        String username = email.split("@")[0];
                        try {
                            UserDto user = new UserDto(name, email, null, "USER", username, true, true);
                            userService.create(user);
                            res.put(email, "OK");
                        } catch (Exception e) {
                            log.error("Error creating user:"+email, e);
                            res.put(email, "ERROR");
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return res;
    }


    /**
     * Send reminder email
     *
     * @return
     */
    @RequestMapping(value = "/betReminder", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<String> sendReminderBetEmail(boolean sendEmail, String emailBody, String emailSubject) {
        Map<Integer, Long> betUsers = encryptedBetService.list().stream()
            .collect(Collectors.groupingBy(EncryptedBetDto::getUserId, Collectors.counting()));
        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

        String allowedMatchDays = deadlineRepository.findActiveDeadline(now, now)
            .getAllowedMatchDays();
        //the configured days that are allowed to get bets for
        List<Integer> allowedDays = Arrays.stream(allowedMatchDays.split(","))
            .map(Integer::parseInt).collect(Collectors.toList());

        long matchNum = allowedDays.stream()
            .mapToLong(day -> gameRepository.findByMatchDay(day).size())
            .sum();

        Set<UserDto> missingBetUsers = userService.list().stream()
            .filter(userDto -> userDto.getRole().equals("USER"))
            .filter(UserDto::getEligible)
            .filter(user -> betUsers.getOrDefault(user.getId(), 0L) < matchNum)
            .collect(Collectors.toSet());

        String text = "This is a reminder that you have not placed your bets for match days:" + allowedDays + ". ";

        if(sendEmail) {
            missingBetUsers.forEach(user -> {
                emailSender.sendEmail(user.getEmail(), emailSubject, text + emailBody, true);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
            });

        }

        return missingBetUsers.stream().map(UserDto::getUsername).collect(Collectors.toList());
    }

}
