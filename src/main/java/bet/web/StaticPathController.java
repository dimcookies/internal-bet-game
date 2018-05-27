package bet.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;

@Controller
public class StaticPathController {

	@GetMapping("/login")
	public String login() {
		return "login";
	}


}
