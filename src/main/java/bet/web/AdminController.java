package bet.web;

import bet.api.dto.GameDto;
import bet.service.livefeed.LiveScoreFeedScheduler;
import bet.service.mgmt.EncryptedBetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
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

    /**
     * Manually perform a score update from the live feed
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/liveupdate", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public String liveupdate() throws Exception {
        liveScoreFeedScheduler.getLiveScores(false);
        return "OK";
    }

    /**
     * Manually update a game
     * @param gameDto
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/manualScoreUpdate", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public String manualScoreUpdate(@RequestBody GameDto gameDto) throws Exception {
        liveScoreFeedScheduler.checkMatchChanged(gameDto);
        liveScoreFeedScheduler.setLastUpdateDate(ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")));
        return "OK";
    }

    /**
     * Move encrypted bets to public available
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/decryptandmove", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String decryptandmove() throws Exception {
        encryptedBetService.decryptAndCopy();
        return "Ok";
    }

}
