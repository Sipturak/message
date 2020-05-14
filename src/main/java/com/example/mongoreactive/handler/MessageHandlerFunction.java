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

import java.time.LocalDate;

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

    public Mono<ServerResponse> saveMessage(ServerRequest serverRequest){
        Mono<Message> data = serverRequest
                .bodyToMono(Message.class)
                .map(message -> {
                    message.setLocalDate(LocalDate.now());
                    //get authentication principal
                    //map to user dto
                    //set user for meessage
                    return message;
                })
                .flatMap(this.messageRepository::save);
        return ServerResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(data, Message.class);
    }

    public Mono<ServerResponse> deleteMessageById(ServerRequest serverRequest){
        String id = serverRequest.pathVariable("id");
        this.messageRepository.deleteById(id);
        return ServerResponse.noContent().build();
    }



}
