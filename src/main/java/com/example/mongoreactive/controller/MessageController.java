package com.example.mongoreactive.controller;

import com.example.mongoreactive.bean.CustomUser;
import com.example.mongoreactive.bean.Message;
import com.example.mongoreactive.bean.UserDto;
import com.example.mongoreactive.repository.MessageRepository;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.function.Consumer;

@Controller
@RequestMapping("/message")
@Log4j2
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Flux<Message> getAllMessages(){
        return this.messageRepository.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Mono<Message> getMessaById(@PathVariable String id){
        return this.messageRepository.findById(id);
    }

    @PostMapping(value = "/update/{id}")
    public Mono<String> update (@PathVariable String id , @ModelAttribute @Valid Message message, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors());
            return Mono.just("new");
        } else {
            message.setLocalDate(LocalDate.now());
            return Mono.just(message)
                    .flatMap(this.messageRepository::save)
                    .doOnSuccess(new Consumer<Message>() {
                        @Override
                        public void accept(Message message) {
                            log.info("Message is updated: " + message);
                        }
                    })
                    .then(Mono.just("redirect:/index"));
        }
    }

    @PostMapping
    public Mono<String> saveMessage (@ModelAttribute @Valid Message message, BindingResult bindingResult, Model model, @AuthenticationPrincipal Mono<CustomUser> principal){
        if(bindingResult.hasErrors()){
            log.info(bindingResult.getAllErrors());
            bindingResult.getFieldErrors().forEach(item->{
                model.addAttribute(item.getField(), item.getDefaultMessage());
            });
            return Mono.just("redirect:/new");
        }
        else {
            message.setLocalDate(LocalDate.now());
            return Mono.just(message)
                    .map(message1 -> {
                        principal.map(UserDto::new)
                                .subscribe(message1::setUserDto);
                        log.info(message);
                        return message;
                    })
                    .flatMap(this.messageRepository::save)
                    .then(Mono.just("redirect:/index"));
        }
    }

    @GetMapping(value = "/page/{id}")
    public Mono<String> findMessage(@PathVariable String id, Model model){
        return Mono.just(id)
                .flatMap(this.messageRepository::findById)
                .doOnNext(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) {
                        log.info("Message adding in model " + message);
                        model.addAttribute("isEdit" , true);
                        model.addAttribute("message", message);
                    }
                })
                .doOnSuccess(log::info)
                .then(Mono.just("new"));
    }

    @DeleteMapping(value = "/{id}")
    public Mono<Void> deleteMessageById(@PathVariable String id){
        return this.messageRepository.deleteById(id)
                .doOnSuccess(log::info);
    }
}
