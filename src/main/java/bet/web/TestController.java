package bet.web;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;

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
