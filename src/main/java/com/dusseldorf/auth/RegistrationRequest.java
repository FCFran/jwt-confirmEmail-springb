package com.dusseldorf.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
public class RegistrationRequest {

    @NotEmpty(message = "firstname is mandatory")
    @NotBlank(message = "firstname is mandatory")
    private String firstname;
    @NotEmpty(message = "lastname is mandatory")
    @NotBlank(message = "lastname is mandatory")
    private String lastname;
    @Email(message = "Email is not formatted")
    @NotEmpty(message = "Email is mandatory")
    @NotBlank(message = "Email is mandatory")
    private String email;
    @NotEmpty(message = "Password is mandatory")
    @NotBlank(message = "Password is mandatory")
    @Length(min = 8, message = "Password should be 8 characters long minimum")
    private String password;

    //https://www.baeldung.com/rest-localized-validation-messages
    //https://danielme.com/2018/10/10/validaciones-con-hibernate-validator/ -> ValidationMessages

    //https://www.machinet.net/tutorial-es/effective-use-of-java-annotations-in-spring-boot-validation -> anotaciones personalizadas
    //https://programandoointentandolo.com/2019/03/crear-un-validador-personalizado-en-spring.html
    //https://www.youtube.com/watch?v=_m1W6n4B4BQ -> mensajes personalizados para create update delete
}
