package edu.nsbm.phishguard.service;

import edu.nsbm.phishguard.entity.AppUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

public interface AppUserService {
    AppUser syncUser(JwtAuthenticationToken token);
    AppUser getCurrentUser(JwtAuthenticationToken token);
}
