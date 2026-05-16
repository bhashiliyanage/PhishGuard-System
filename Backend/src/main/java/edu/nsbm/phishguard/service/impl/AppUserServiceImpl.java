package edu.nsbm.phishguard.service.impl;

import edu.nsbm.phishguard.entity.AppUser;
import edu.nsbm.phishguard.repository.AppUserRepository;
import edu.nsbm.phishguard.service.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository userRepository;

    @Override
    public AppUser syncUser(JwtAuthenticationToken token) {
        String keycloakId = token.getToken().getSubject();
        String email      = token.getToken().getClaimAsString("email");
        String username   = token.getToken().getClaimAsString("preferred_username");
        String firstName  = token.getToken().getClaimAsString("given_name");
        String lastName   = token.getToken().getClaimAsString("family_name");

        return userRepository.findById(keycloakId).orElseGet(() -> {
            log.info("New user synced from Keycloak: {}", username);
            AppUser newUser = new AppUser();
            newUser.setId(keycloakId);
            newUser.setEmail(email);
            newUser.setUsername(username);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            return userRepository.save(newUser);
        });
    }

    @Override
    public AppUser getCurrentUser(JwtAuthenticationToken token) {
        String keycloakId = token.getToken().getSubject();
        return userRepository.findById(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found, please sync first"));
    }
}
