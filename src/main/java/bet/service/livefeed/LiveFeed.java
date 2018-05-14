package bet.service.livefeed;

import bet.api.dto.GameDto;

import java.util.List;

public interface LiveFeed {

	public List<GameDto> getLiveFeed();

}