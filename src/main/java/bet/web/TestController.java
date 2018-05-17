package bet.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import twitter4j.*;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@RestController
public class TestController {

	private final ServletContext servletContext;

	public TestController(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

//	@RequestMapping(value = "/test/live_scores", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//	public String liveScore() throws Exception {
//		String result = new BufferedReader(
//				new InputStreamReader(getClass().getClassLoader().getResourceAsStream("games.json"))).lines()
//						.collect(Collectors.joining("\n"));
//
//		return result;
//
//	}

}
