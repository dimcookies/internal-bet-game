package bet.service.livefeed;

import bet.api.constants.GameStatus;
import bet.api.dto.GameDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of live feed using football-api-org for scores
 */
@Component
@Profile("lifefeed-footballapiorgnew")
public class FootballApiOrgNewLiveFeedImpl extends FootballApiOrgLiveFeedImpl {

    @Override
    public List<GameDto> getLiveFeed() {
        String response = getResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode fixtures;
        try {
            fixtures = objectMapper.readTree(response).get("fixtures");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Streams.stream(fixtures.iterator()).map(jsonNode -> {
                GameDto dto = new GameDto(GameStatus.valueOf(jsonNode.get("status").textValue()),
                    jsonNode.get("matchday").intValue(),
                    jsonNode.get("homeTeamName").textValue(), 0,
                    jsonNode.get("awayTeamName").textValue(), 0,
                    jsonNode.get("date").textValue(),
                    jsonNode.get("result").get("goalsHomeTeam").asInt(0),
                    jsonNode.get("result").get("goalsAwayTeam").asInt(0));
                dto.setId(extractGameId(jsonNode.get("_links").get("self").get("href").textValue()));

                return dto;
        }).collect(Collectors.toList());
    }

    private int extractGameId(String link) {
        String[] ar = link.split("/");
        return Integer.valueOf(ar[ar.length-1]);
    }


    private String getResponse() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
        if(token != null && token.length() > 0) {
            headers.add("X-Auth-Token", token);
        }

        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(liveFeedUrl, HttpMethod.GET, requestEntity, String.class);
        return responseEntity.getBody();
    }

}