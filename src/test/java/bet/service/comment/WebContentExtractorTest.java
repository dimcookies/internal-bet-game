package bet.service.comment;

import bet.base.AbstractBetTest;
import org.junit.Test;

import java.util.List;

public class WebContentExtractorTest extends AbstractBetTest {

    private WebContentExtractor webContentExtractor = new WebContentExtractor();

	@Test
	public void test_noMatch() {
        List<String> urls = webContentExtractor.pullLinks("this is a string");
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
        List<String> urls = webContentExtractor.pullLinks("Text https://img.in.gr/ https://www.cnn.com/ Text");
		assertEquals(2, urls.size());
	}

	private void test_singleMatch(String text, String url) {
        List<String> urls = webContentExtractor.pullLinks(text);
		assertEquals(1, urls.size());
		assertEquals(url, urls.get(0));
	}

    @Test
    public void test_containsHtml_positive() {
        assertTrue(webContentExtractor.stringContainsHtml("This is a test <b>test</b> Test"));
        assertTrue(webContentExtractor.stringContainsHtml("This is a test <br/>"));
        assertTrue(webContentExtractor.stringContainsHtml("This is a test < br attr=\"a\"/>"));
    }

    @Test
    public void test_containsHtml_malformed() {
        assertTrue(webContentExtractor.stringContainsHtml("This is a test <b>Test"));
    }

    @Test
    public void test_containsHtml_simpleText() {
        assertFalse(webContentExtractor.stringContainsHtml("This is a test"));
    }

}