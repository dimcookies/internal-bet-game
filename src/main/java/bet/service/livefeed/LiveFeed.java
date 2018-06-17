package bet.service.livefeed;

import bet.api.dto.GameDto;

import java.util.List;

/**
 * Interface for live score feed implementations. Get the
 * latest scores for the matches
 */
public interface LiveFeed {

    List<GameDto> getLiveFeed();

}