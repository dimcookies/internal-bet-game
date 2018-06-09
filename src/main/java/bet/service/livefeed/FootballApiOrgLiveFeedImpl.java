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

/**
 * Implementation of live feed using football-api-org for scores
 */
@Component
@Profile("lifefeed-footballapiorg")
public class FootballApiOrgLiveFeedImpl implements LiveFeed {

	@Value("${application.live_feed.footballapiorg.url}")
	protected String liveFeedUrl;

	@Value("${application.live_feed.footballapiorg.token}")
	protected String token;

	@Autowired
	protected RestTemplate restTemplate;

	@Override
	public List<GameDto> getLiveFeed() {
		HttpEntity entity = getHeaders();

		ResponseEntity<GameDto[]> responseEntity = restTemplate.exchange(
				liveFeedUrl, HttpMethod.GET, entity, GameDto[].class);

		return Arrays.asList(responseEntity.getBody());
	}

	protected HttpEntity getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Auth-Token", token);

		return new HttpEntity(headers);
	}
}