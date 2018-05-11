package bet.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@RestController
public class VersionController {

	private final ServletContext servletContext;

	public VersionController(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@RequestMapping(value = "/version", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public String version() throws Exception {
		String result = new BufferedReader(
				new InputStreamReader(getClass().getClassLoader().getResourceAsStream("static/version"))).lines()
						.collect(Collectors.joining("\n"));

		return result;

	}
}
