package edu.nsbm.phishguard.controller;

import edu.nsbm.phishguard.entity.AppUser;
import edu.nsbm.phishguard.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService userService;

    @PostMapping("/sync")
    public AppUser sync(JwtAuthenticationToken token) {
        return userService.syncUser(token);
    }

    @GetMapping("/me")
    public AppUser getCurrentUser(JwtAuthenticationToken token) {
        return userService.getCurrentUser(token);
    }
}
