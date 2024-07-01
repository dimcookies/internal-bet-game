package bet.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@GetMapping("/user/form")
	public String createUser() {
		return "createuser";
	}

}
