package com.sockib.notesapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Controller;

@Controller
public class WebConfig {

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .csrf(x -> x.disable())
                .cors(x -> x.disable())
//                .formLogin()
                .authorizeHttpRequests(x -> x.anyRequest().permitAll())
                .build();
    }

}
