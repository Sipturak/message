package com.example.mongoreactive;

import com.example.mongoreactive.bean.Message;
import com.example.mongoreactive.handler.MessageHandlerFunction;
import com.example.mongoreactive.handler.UserHandlerFunction;
import com.example.mongoreactive.repository.MessageRepository;
import lombok.Value;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDate;
import java.util.function.Consumer;

@SpringBootApplication
@Log4j2
public class MongoReactiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(MongoReactiveApplication.class, args);
	}

	@EnableWebFluxSecurity
	public static class CustomSpringSecurityConfig {

		@Bean
		SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity){
			serverHttpSecurity
					.csrf().disable()
					.authorizeExchange()
						.pathMatchers( "/login", "/webjars/**","/login-error","/register").permitAll()
						.pathMatchers(HttpMethod.POST, "/user/register").permitAll()
						.pathMatchers(HttpMethod.DELETE, "/user/delete").permitAll()
						.anyExchange()
						.authenticated()
						.and().formLogin()
						.loginPage("/login")
					.authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/index"))
					.authenticationFailureHandler(new RedirectServerAuthenticationFailureHandler("/login-error"))
                        .and().logout().logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler("/login"));
			return serverHttpSecurity.build();
		}

		public ServerLogoutSuccessHandler logoutSuccessHandler(String uri) {
            RedirectServerLogoutSuccessHandler successHandler = new RedirectServerLogoutSuccessHandler();
            successHandler.setLogoutSuccessUrl(URI.create(uri));
            return successHandler;
        }

		@Bean
		PasswordEncoder passwordEncoder (){
			return PasswordEncoderFactories.createDelegatingPasswordEncoder();
		}
	}

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private MessageHandlerFunction messageHandlerFunction;

	@Autowired
	private UserHandlerFunction userHandlerFunction;

	@Bean
	ApplicationRunner runner () {
		return args -> {
			this.messageRepository.deleteAll();
		};
	}

	@Bean
	RouterFunction <?> routerFunction (){
		return  RouterFunctions.route()
				.GET("messages", this.messageHandlerFunction::getAllMessages)
				.POST("message", this.messageHandlerFunction::saveMessage)
				.GET("message/{id}", this.messageHandlerFunction::getMessageById)
				.DELETE("message/{id}", this.messageHandlerFunction::deleteMessageById)
				.GET("user", this.userHandlerFunction::findAllUsers)
				.GET("user/{id}", this.userHandlerFunction::findUserById)
				.POST("user", this.userHandlerFunction::registerUser)
		.build();
	}


}
