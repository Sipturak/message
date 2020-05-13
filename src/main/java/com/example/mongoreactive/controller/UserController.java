package com.example.mongoreactive.controller;

import com.example.mongoreactive.bean.CustomUser;
import com.example.mongoreactive.bean.UserDto;
import com.example.mongoreactive.repository.UserReaciveRepository;
import com.example.mongoreactive.validator.CustomUserValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import javax.validation.Valid;
import java.util.List;
import java.util.function.Consumer;

@Controller
@RequestMapping("/user")
@Log4j2
public class UserController {

    @Autowired
    private UserReaciveRepository userReaciveRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomUserValidator customUserValidator;

    @PostMapping(value = "/register")
    public Mono<String> save(@ModelAttribute @Valid UserDto userDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", true);
            List<FieldError> errors = bindingResult.getFieldErrors();
            errors.stream()
                    .peek(item -> {
                        model.addAttribute(item.getField() + "Error", item.getDefaultMessage());
                    }).forEach(log::info);
            return Mono.just("register");
        } else {
            //check if user in db has same username or password or email(optional)
            return  this.userReaciveRepository.findByUsername(userDto.getUsername())
                    .flatMap(body -> {
                        Errors errors = new BeanPropertyBindingResult(body,UserDto.class.getName());
                        this.customUserValidator.validate(body,errors);
                        log.info(errors.getAllErrors());
                        errors.getAllErrors().forEach(objectError -> {
                            model.addAttribute("error", true);
                            model.addAttribute( objectError.getCode() + "Error" , objectError.getDefaultMessage());
                        });
                        return Mono.just("register");
                    })
                    .switchIfEmpty(Mono.defer(() -> {
                        return  Mono.just(userDto)
                                .doOnSuccess(data-> userDto.setPassword(this.passwordEncoder.encode(data.getPassword())))
                                .subscribeOn(Schedulers.parallel())
                                .flatMap(this.userReaciveRepository::save)
                                .doOnNext(log::info)
                                .then(Mono.just("redirect:/login"));
                    }));
        }
    }

    @GetMapping(value = "/{id}")
    public Mono<String> findUserById(@PathVariable String id, Model model){
        model.addAttribute("isProfile" , true);
        return  this.userReaciveRepository
                            .findById(id)
                            .doOnNext(userDto -> model.addAttribute("userDto",userDto))
                            .doOnSuccess(log::info)
                    .then(Mono.just("profile"));
    }

    @GetMapping
    @ModelAttribute("users")
    public Flux<UserDto> findAllUsers(){
        return this.userReaciveRepository.findAll();
    }

//    @PostMapping(value = "/update/{id}")
//    public Mono<String> updateUserById(@PathVariable String id,  @ModelAttribute @Valid  UserDto userDto, BindingResult bindingResult, Model model) {
//        if (bindingResult.hasErrors()) {
//            log.info(bindingResult.getAllErrors());
//            return Mono.just("profile");
//        }
//        else {
//            return this.userReaciveRepository
//                    .findById(id)
//                    .map(userDto1 -> {
//                        userDto.setId(userDto1.getId());
//                        return userDto;
//                    })
//                    .flatMap(this.userReaciveRepository::save)
//                    .doOnSuccess(new Consumer<UserDto>() {
//                        @Override
//                        public void accept(UserDto userDto) {
//                            log.info("Update profile user");
//                            log.info(userDto);
//                        }
//                    })
//                    .then(Mono.just("redirect:/index"));
//        }
//    }

    @DeleteMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Mono<Boolean> deletAll(){
        return this.userReaciveRepository.deleteAll()
                .then(Mono.just(true));
    }
}
