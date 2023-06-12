package com.jimmy.swiftwheels.user;

import com.jimmy.swiftwheels.role.Role;
import com.jimmy.swiftwheels.util.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping({"/register"})
    public User register(@RequestBody RegisterRequest request) {
        System.out.println("Received request: " + request);
        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();

        return userService.register(user);
    }
//
//    @GetMapping({"/forAdmin"})
//    @PreAuthorize("hasRole('Admin')")
//    public String forAdmin(){
//        return "This URL is only accessible to the admin";
//    }
//
//    @GetMapping({"/forUser"})
//    @PreAuthorize("hasRole('User')")
//    public String forUser(){
//        return "This URL is only accessible to the user";
//    }
}
