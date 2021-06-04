package bet.web;

import bet.api.dto.GameDto;
import bet.api.dto.UserDto;
import bet.model.Game;
import bet.model.Odd;
import bet.model.User;
import bet.repository.GameRepository;
import bet.repository.OddRepository;
import bet.repository.UserRepository;
import bet.service.analytics.AnalyticsScheduler;
import bet.service.livefeed.LiveScoreFeedScheduler;
import bet.service.mgmt.EncryptedBetService;
import bet.service.mgmt.UserService;
import bet.service.rss.RssFeedScheduler;
import bet.service.utils.EhCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

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

    @Autowired
    private RssFeedScheduler rssFeedScheduler;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private OddRepository oddRepository;

    @Autowired
    private GameRepository gameRepository;

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
        liveScoreFeedScheduler.checkMatchChanged(gameDto);
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
        rssFeedScheduler.getRssFeed();
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



}
