package bet.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;

/**
 * Static mappings of web services
 */
@Controller
public class StaticPathController {

	/**
	 * Login page
	 *
	 * @return
	 */
	@GetMapping("/login")
	public String login() {
		return "login";
	}


}
