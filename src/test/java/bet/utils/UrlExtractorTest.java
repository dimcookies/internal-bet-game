package bet.utils;

import bet.api.constants.GameStatus;
import bet.base.AbstractBetIntegrationTest;
import bet.base.AbstractBetTest;
import bet.repository.GameRepository;
import bet.service.GamesInitializer;
import bet.service.livefeed.FifaComLiveFeedImpl;
import bet.service.utils.GamesSchedule;
import bet.service.utils.UrlExtractor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;

public class UrlExtractorTest extends AbstractBetTest {

	private UrlExtractor urlExtractor = new UrlExtractor();

	@Test
	public void test_noMatch() {
		List<String> urls = urlExtractor.pullLinks("this is a string");
		assertEquals(0, urls.size());
	}

	@Test
	public void test_singleUrl() {
		test_singleMatch("this is a string http://www.in.gr text", "http://www.in.gr/");
	}

	@Test
	public void test_singleUrl_start() {
		test_singleMatch("http://www.in.gr text", "http://www.in.gr/");
	}

	@Test
	public void test_singleUrl_end() {
		test_singleMatch("Text http://www.in.gr", "http://www.in.gr/");
	}

	@Test
	public void test_singleUrl_nohttp() {
		test_singleMatch("Text www.in.gr Text", "http://www.in.gr/");
	}

	@Test
	public void test_singleUrl_nodomain() {
		test_singleMatch("Text img.in.gr/test1/test2 Text", "http://img.in.gr/test1/test2");
	}

	@Test
	public void test_singleUrl_https() {
		test_singleMatch("Text https://img.in.gr/ Text", "https://img.in.gr/");
	}

	@Test
	public void test_multipleUrl() {
		List<String> urls = urlExtractor.pullLinks("Text https://img.in.gr/ https://www.cnn.com/ Text");
		assertEquals(2, urls.size());
	}

	private void test_singleMatch(String text, String url) {
		List<String> urls = urlExtractor.pullLinks(text);
		assertEquals(1, urls.size());
		assertEquals(url, urls.get(0));
	}

}