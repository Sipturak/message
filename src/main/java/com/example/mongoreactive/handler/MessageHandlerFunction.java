package com.example.mongoreactive.handler;

import com.example.mongoreactive.repository.MessageRepository;
import com.example.mongoreactive.bean.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
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



}
