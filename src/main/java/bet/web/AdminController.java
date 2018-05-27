package bet.web;

import bet.service.livefeed.LiveScoreFeedScheduler;
import bet.service.mgmt.EncryptedBetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
public class AdminController {

    @Autowired
    private LiveScoreFeedScheduler liveScoreFeedScheduler;

    @Autowired
    private EncryptedBetService encryptedBetService;

    @RequestMapping(value = "/liveupdate", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public String liveupdate() throws Exception {
        liveScoreFeedScheduler.getLiveScores(false);
        return "OK";
    }

    @RequestMapping(value = "/decryptandmove", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String decryptandmove() throws Exception {
        encryptedBetService.decryptAndCopy();
        return "Ok";
    }

}
