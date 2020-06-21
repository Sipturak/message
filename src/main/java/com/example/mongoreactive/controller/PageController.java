package com.example.mongoreactive.controller;

import com.example.mongoreactive.bean.CustomUser;
import com.example.mongoreactive.bean.Message;
import com.example.mongoreactive.bean.UserDto;
import com.example.mongoreactive.repository.MessageRepository;
import com.example.mongoreactive.repository.UserReaciveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.method.support.ModelAndViewContainer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Consumer;

@Controller
public class PageController {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserReaciveRepository userReaciveRepository;

    @GetMapping(value = "/login")
    public String loginPage (){
        return "login";
    }

    @GetMapping("/login-error")
    public String loginErrorPage(Model model){
        model.addAttribute("error", true);
        model.addAttribute("errorMessage", "Bad credentials");
        return "login";
    }

    @GetMapping(value = "/home")
    public String getHomePage (){
        return "home";
    }

    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute("userDto", new UserDto());
        return "register";
    }

    @GetMapping(value = "/index")
    public String getIndegPage(){
        return  "index";
    }

    @GetMapping(value = "/new")
    public Mono<String> getNew(@ModelAttribute Message message, Model model, @AuthenticationPrincipal CustomUser customUser){
        return  this.userReaciveRepository.findAll()
                .filter(userDto -> {
                    if(customUser.getUsername().equals(userDto.getUsername()))
                        return false;
                    return true;
                })
                .collectList()
                .doOnNext(new Consumer<List<UserDto>>() {
                    @Override
                    public void accept(List<UserDto> userDtos) {
                        model.addAttribute("users", userDtos);
                        model.addAttribute("isNew", true);

                    }
                })
                .then(Mono.just("new"));
    }


}
