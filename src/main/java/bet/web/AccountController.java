package bet.web;

import bet.api.dto.UserDto;
import bet.exception.UserAlreadyExistException;
import bet.service.mgmt.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Controller
@RequestMapping("/accounts/")
public class AccountController {

    @Autowired
    private UserService userService;

    private final Pattern corporateEmailPattern = Pattern.compile("^(.+)@upstreamsystems.com$");

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String registerUser(String email, RedirectAttributes redirectAttributes) {
        try {
            Matcher matcher = corporateEmailPattern.matcher(email);
            if (!matcher.matches()) {
                log.error("Invalid email:" + email);
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid email: " + email + ". Please use your corporate email address.");
                return "redirect:/register";
            }
            String username = matcher.group(1);
            String name = WordUtils.capitalizeFully(username.replaceAll("[^a-zA-Z]", " "));
            UserDto user = new UserDto(name, email, null, "USER", username, true, true);
            userService.create(user);
        } catch (UserAlreadyExistException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An account with this email already exists: " + email +
                    ". Please check your inbox or contact the administrator.");
            return "redirect:/register";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            return "redirect:/register";
        }
        redirectAttributes.addFlashAttribute("message", "Your account has been created. An email with credentials has been sent to " + email);
        return "redirect:/login";
    }

}
