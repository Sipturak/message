package com.example.mongoreactive.validator;

import com.example.mongoreactive.bean.UserDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.function.Consumer;

@Component
@Log4j2
public class CustomUserValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return UserDto.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        UserDto customUser = (UserDto) o;
            if(customUser != null){
                errors.reject("username" , "error message");
            }
        }
}
