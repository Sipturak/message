package com.example.mongoreactive.service;

import com.example.mongoreactive.bean.CustomUser;
import com.example.mongoreactive.repository.UserReaciveRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomReactiveUserService implements ReactiveUserDetailsService {

    @Autowired
    private UserReaciveRepository userReaciveRepository;

    @Override
    public Mono<UserDetails> findByUsername(String s) {
        Mono<UserDetails> user = this.userReaciveRepository.findByUsername(s)
                .map(CustomUser::new);
        System.out.println(user.doOnSuccess(System.out::println));
        return user;

    }
}
