package com.example.mongoreactive.controller;

import com.example.mongoreactive.bean.CustomUser;
import com.example.mongoreactive.bean.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.function.Consumer;

@ControllerAdvice
public class PrincipalAdvice {

    @ModelAttribute("currentUser")
    public Mono<CustomUser> getUser(@AuthenticationPrincipal Mono<CustomUser> principalMono){
        return principalMono;
    }

}
