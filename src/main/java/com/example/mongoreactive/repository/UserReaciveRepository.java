package com.example.mongoreactive.repository;

import com.example.mongoreactive.bean.UserDto;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserReaciveRepository extends ReactiveCrudRepository<UserDto, String> {
    Mono<UserDto> findByUsername(String username);

}
