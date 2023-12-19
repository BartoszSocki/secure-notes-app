package com.sockib.notesapp.service.impl;

import com.sockib.notesapp.model.entity.AppUser;
import com.sockib.notesapp.model.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findVerifiedUserByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
        UserDetails userDetails = User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .disabled(!user.getIsVerified())
                .build();

        return userDetails;
    }

}
