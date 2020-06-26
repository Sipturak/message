package com.example.mongoreactive.handler;

import com.example.mongoreactive.bean.CustomUser;
import com.example.mongoreactive.bean.UserDto;
import com.example.mongoreactive.repository.MessageRepository;
import com.example.mongoreactive.bean.Message;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
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
        System.out.println("Id is " + id);
        Mono<Message> messageMono = this.messageRepository.findById(id);

        return ServerResponse
                .status(HttpStatus.FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(messageMono, Message.class);
    }

    public Mono<ServerResponse> getMessageByUsername(ServerRequest serverRequest){
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(new IllegalStateException("Reactive Security context is empty")))
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .flatMap(s->{
                    CustomUser customUser = (CustomUser) s;
                    return Mono.just(this.messageRepository.findByMessageOwner(customUser.getUsername()));
                })
                .doOnSuccess(new Consumer<Flux<Message>>() {
                    @Override
                    public void accept(Flux<Message> messageMono) {
                        messageMono.collectList().doOnSuccess(log::info);
                    }
                })
                .flatMap(o -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(o, Message.class));
    }

    public Mono<ServerResponse> saveMessage(ServerRequest serverRequest){

        final String pathVariable = serverRequest.pathVariable("username");
        return ServerResponse.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        serverRequest.bodyToMono(Message.class)
                                .map(message -> {
                                       message.setLocalDate(LocalDate.now());
                                       message.setMessageOwner(pathVariable);
                                       return message;
                                })
                                .flatMap(this.messageRepository::save)
                        , Message.class);
    }

    public Mono<ServerResponse> deleteMessageById(ServerRequest serverRequest){
        String id = serverRequest.pathVariable("id");
        log.info("Id of message is " + id);
        this.messageRepository.deleteById(id).subscribe();
        return ServerResponse.noContent().build();
    }
}
