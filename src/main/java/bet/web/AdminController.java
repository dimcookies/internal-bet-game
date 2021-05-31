package bet.web;

import bet.api.dto.GameDto;
import bet.api.dto.UserDto;
import bet.model.User;
import bet.repository.UserRepository;
import bet.service.analytics.AnalyticsScheduler;
import bet.service.livefeed.LiveScoreFeedScheduler;
import bet.service.mgmt.EncryptedBetService;
import bet.service.mgmt.UserService;
import bet.service.rss.RssFeedScheduler;
import bet.service.utils.EhCacheUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Helper services for administration tasks
 */
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





}
