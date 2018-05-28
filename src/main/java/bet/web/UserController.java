package bet.web;

import bet.api.dto.UserDto;
import bet.model.Friend;
import bet.model.User;
import bet.repository.FriendRepository;
import bet.repository.UserRepository;
import bet.service.mgmt.UserService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    /**
     * Update currently logged in user user friends
     * @param usernames
     * @param principal
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/friends/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Friend> updateFriends(@RequestBody List<String> usernames, Principal principal) throws Exception {
        User user = userRepository.findOneByName(principal.getName());
        //delete current values
        friendRepository.deleteByUser(user);

        List<Friend> friends = usernames.stream().map(username -> {
            User friend = userRepository.findOneByName(username);
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
        User user = userRepository.findOneByName(principal.getName());
        return friendRepository.findByUser(user);
    }

    /**
     * Change password of currently logged in user
     * @param password
     * @param principal
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public String changePassword(@RequestParam(value = "password", required = true) String password, Principal principal) throws Exception {
        if(password == null || password.length() == 0) {
            throw new RuntimeException();
        }
        User user = userRepository.findOneByName(principal.getName());
        UserDto userDto = new UserDto();
        userDto.fromEntity(user);
        userDto.setPassword(password);
        userService.update(userDto);
        return "OK";
    }

    /**
     * Get list of usernames of users
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> participants() throws Exception {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(User::getName).collect(Collectors.toList());
    }

}
