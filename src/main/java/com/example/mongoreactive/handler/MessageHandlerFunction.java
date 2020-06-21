package com.example.mongoreactive.handler;

import com.example.mongoreactive.bean.CustomUser;
import com.example.mongoreactive.bean.UserDto;
import com.example.mongoreactive.repository.MessageRepository;
import com.example.mongoreactive.bean.Message;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.time.LocalDate;
import java.util.function.Consumer;

@Component
@Log4j2
public class MessageHandlerFunction {

    @Autowired
    private MessageRepository messageRepository;

    public Mono<ServerResponse> getAllMessages (ServerRequest serverRequest){
        return ServerResponse
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.messageRepository.findAll(), Message.class);
    }

    public Mono<ServerResponse> getMessageById(ServerRequest serverRequest){
        String id = serverRequest.pathVariable("id");
        System.out.println(id);
        Mono<Message> messageMono = this.messageRepository.findById(id);
        return ServerResponse
                .status(HttpStatus.FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(messageMono, Message.class);
    }

    public Mono<ServerResponse> saveMessage(ServerRequest serverRequest){
        return serverRequest.principal()
                .map(Principal::getName)
                .flatMap(s -> {
                   return ServerResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON)
                           .body(
                                   serverRequest.bodyToMono(Message.class)
                                   .map(message -> {
                                       message.setLocalDate(LocalDate.now());
                                       message.setMessageOwner(s);
                                       return message;
                                   })
                                   .flatMap(this.messageRepository::save)
                                   , Message.class);
                });
    }

    public Mono<ServerResponse> deleteMessageById(ServerRequest serverRequest){
        String id = serverRequest.pathVariable("id");
        log.info("Id of message is " + id);
        this.messageRepository.deleteById(id).subscribe();
        return ServerResponse.noContent().build();
    }
}
