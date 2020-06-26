package com.example.mongoreactive.repository;

import com.example.mongoreactive.bean.Message;
import org.reactivestreams.Publisher;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessageRepository extends ReactiveCrudRepository<Message, String> {

    Flux<Message> findByMessageOwner(String messageOwner);

}
