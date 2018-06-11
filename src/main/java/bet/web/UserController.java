package bet.web;

import bet.api.dto.UserDto;
import bet.model.Friend;
import bet.model.User;
import bet.repository.FriendRepository;
import bet.repository.UserRepository;
import bet.service.mgmt.UserService;
import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Web services related to users
 */
@RestController
@RequestMapping("/users/")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Update currently logged in user user friends
     * @param usernames
     * @param principal
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/friends/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Friend> updateFriends(@RequestBody List<String> usernames, Principal principal) throws Exception {
        User user = userRepository.findOneByUsername(principal.getName());
        //delete current values
        friendRepository.deleteByUser(user);

        List<Friend> friends = usernames.stream().map(username -> {
            User friend = userRepository.findOneByUsername(username);
            if(friend == null) {
                throw new RuntimeException();
            }
            return new Friend(user, friend);
        }).collect(Collectors.toList());

        return Lists.newArrayList(friendRepository.save(friends));
    }

    /**
     * Get currently logged in user friends
     * @param principal
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/friends/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Friend> listFriends(Principal principal) throws Exception {
        User user = userRepository.findOneByUsername(principal.getName());
        return friendRepository.findByUser(user);
    }

    /**
     * Change password of currently logged in user
     * @param password
     * @param principal
     * @return
     * @throws Exception
     */
    @CacheEvict(allEntries = true, cacheNames = {"users1","users2"})
    @RequestMapping(value = "/modify", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public String changePassword(@RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "optOut", required = false) Boolean optOut, Principal principal) throws Exception {
        User user = userRepository.findOneByUsername(principal.getName());
        UserDto userDto = new UserDto();
        userDto.fromEntity(user);
        if(optOut != null) {
            userDto.setOptOut(optOut);
        }
        if(password != null) {
            userDto.setPassword(passwordEncoder.encode(password));
        }
        userService.update(userDto);
        return "OK";
    }

    /**
     * Change password of currently logged in user
     * @param principal
     * @return
     * @throws Exception
     */
    @CacheEvict(allEntries = true, cacheNames = {"users1","users2"})
    @RequestMapping(value = "/modify2", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public String changePassword2(@RequestBody ChangeUserSettingsDto changeUserSettingsDto, Principal principal) throws Exception {
        return changePassword(changeUserSettingsDto.getPassword(), changeUserSettingsDto.getOptOut(), principal);
    }

    /**
     * Get list of usernames of users
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,String> participants() throws Exception {
        return userService.list().stream()
                .collect(Collectors.toMap(UserDto::getUsername, UserDto::getName));

    }

    /**
     * Get current logged in user
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/currentUser", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public User currentUser(Principal principal) throws Exception {
        return userRepository.findOneByUsername(principal.getName());
    }



}

@Data
class ChangeUserSettingsDto {
    private String password;
    private Boolean optOut;

    public ChangeUserSettingsDto() {

    }

    public ChangeUserSettingsDto(String password, Boolean optOut) {
        this.password = password;
        this.optOut = optOut;
    }

    public ChangeUserSettingsDto(String password) {
        this.password = password;
    }

    public ChangeUserSettingsDto(Boolean optOut) {
        this.optOut = optOut;
    }

}
