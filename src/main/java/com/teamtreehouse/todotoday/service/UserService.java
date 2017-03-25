package com.teamtreehouse.todotoday.service;

import com.teamtreehouse.todotoday.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Created by kpfromer on 3/24/17.
 */
public interface UserService extends UserDetailsService {
    User findByUsername(String username);
}
