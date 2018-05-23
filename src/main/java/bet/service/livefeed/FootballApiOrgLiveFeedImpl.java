package bet.service.livefeed;

import bet.api.dto.GameDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("live")
public class FootballApiOrgLiveFeedImpl implements LiveFeed {

	@Value("${application.live_feed.footballapiorg.url}")
	private String liveFeedUrl;

	@Value("${application.live_feed.footballapiorg.token}")
	private String token;

	@Autowired
	private RestTemplate restTemplate;

	@Override public List<GameDto> getLiveFeed() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Auth-Toke", token);

		HttpEntity entity = new HttpEntity(headers);

		ResponseEntity<GameDto[]> responseEntity = restTemplate.exchange(
				liveFeedUrl, HttpMethod.GET, entity, GameDto[].class);

		return Arrays.asList(responseEntity.getBody());
	}
}