package com.example.mongoreactive.bean;

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
public class Message implements Serializable {
    @Id
    private String id;
    @NotEmpty
    private String name;
//    @Size(min = 20, max = 150)
    @NotEmpty
    private String description;
    private LocalDate localDate;
    private String messageOwner;
}
