package com.example.mongoreactive.handler;

import com.example.mongoreactive.bean.CustomUser;
import com.example.mongoreactive.bean.UserDto;
import com.example.mongoreactive.repository.UserReaciveRepository;
import com.example.mongoreactive.validator.CustomUserValidator;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class UserHandlerFunction {

    @Autowired
    private UserReaciveRepository userReaciveRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomUserValidator customUserValidator;

    public Mono<ServerResponse> findUserById(ServerRequest serverRequest){
        String id  = serverRequest.pathVariable("id");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.userReaciveRepository.findById(id), UserDto.class);
    }

    public Mono<ServerResponse> findAllUsers (ServerRequest serverRequest){
        return  ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .flatMap(principle->{
                    CustomUser customUser = (CustomUser) principle;
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.userReaciveRepository.findAll()
                                            .filter(userDto -> !userDto.getUsername().equals(customUser.getUsername()))
                                    ,UserDto.class);
                });
    }

    //update method



}
