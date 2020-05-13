package com.example.mongoreactive.repository;

import com.example.mongoreactive.bean.Message;
import org.reactivestreams.Publisher;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface MessageRepository extends ReactiveCrudRepository<Message, String> {

    Mono<Message> findByName(String name);

}
