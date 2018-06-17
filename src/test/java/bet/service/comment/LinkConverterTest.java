package bet.service.comment;

import bet.base.AbstractBetTest;
import org.junit.Test;

public class LinkConverterTest extends AbstractBetTest {

    private YoutubeLinkConverter youtubeLinkConverter = new YoutubeLinkConverter();
    private ImageLinkConverter imageLinkConverter = new ImageLinkConverter();

    @Test
    public void test_imageConverter_bySuffix() {
        assertTrue(imageLinkConverter.isApplicable("http://www.test.com/test.jpg"));
        assertTrue(imageLinkConverter.isApplicable("http://www.test.com/test.gif"));
        assertTrue(imageLinkConverter.isApplicable("http://www.test.com/test.bmp"));
        assertTrue(imageLinkConverter.isApplicable("http://www.test.com/test.jpg?a=b&c=d"));
    }

    @Test
    public void test_youtubeConverter_isApplicable() {
        assertTrue(youtubeLinkConverter.isApplicable("http://www.youtube.com/watch?v=abc"));
        assertTrue(youtubeLinkConverter.isApplicable("http://y2u.be/abc"));
        assertTrue(youtubeLinkConverter.isApplicable("http://youtu.be/abc"));
        assertFalse(youtubeLinkConverter.isApplicable("http://test.com/test"));
    }

    @Test
    public void test_youtubeConverter_extractUrl() {
        assertEquals("abc", youtubeLinkConverter.extractVideoUrl("http://www.youtube.com/watch?v=abc"));
        assertEquals("abc", youtubeLinkConverter.extractVideoUrl("http://www.youtube.com/watch?c=1&v=abc"));
        assertEquals("abc", youtubeLinkConverter.extractVideoUrl("http://www.youtube.com/watch?v=abc&c=1"));
        assertEquals("abc", youtubeLinkConverter.extractVideoUrl("http://y2u.be/abc"));
        assertEquals("abc", youtubeLinkConverter.extractVideoUrl("http://youtu.be/abc"));
    }


}