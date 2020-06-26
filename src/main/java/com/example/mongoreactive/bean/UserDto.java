package com.example.mongoreactive.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Document
public class UserDto implements Serializable {

    public UserDto(UserDto userDto){
        this(userDto.getId(), userDto.getUsername(), userDto.getPassword(), userDto.getEmail(), userDto.getName(),
                userDto.getLastName(), userDto.getSex(), userDto.getAddress(), userDto.getBirthDate(), userDto.postalCode);
    }

    @Id
    private String id;
    @NotEmpty
    private String username;
    @NotEmpty
    @Size(min = 10)
    @JsonIgnore
    private String password;
    private String email;
    private String name;
    private String lastName;
    private String sex;
    private String address;
    private LocalDate birthDate;
    private Integer postalCode;

}
