package com.example.mongoreactive.handler;

import com.example.mongoreactive.bean.UserDto;
import com.example.mongoreactive.repository.UserReaciveRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class UserHandlerFunction {

    @Autowired
    private UserReaciveRepository userReaciveRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Mono<ServerResponse> registerUser (ServerRequest serverRequest){

        Mono<UserDto> body = serverRequest.bodyToMono(UserDto.class);
        body.flatMap(userDto -> this.userReaciveRepository.save(userDto))
                .doOnSuccess(userDto -> this.passwordEncoder.encode(userDto.getPassword()))
                .subscribeOn(Schedulers.parallel())
                .doOnNext(System.out::println);
        return ServerResponse
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .location(serverRequest.uri())
                .body(body, UserDto.class);
    }

}
