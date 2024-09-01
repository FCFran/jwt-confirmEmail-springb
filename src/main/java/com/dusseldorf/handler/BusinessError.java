package com.dusseldorf.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BusinessError {
    NO_CODE(0, HttpStatus.NOT_IMPLEMENTED, "No code"),
    INCORRECT_CURRENT_PASSWORD(300, HttpStatus.BAD_REQUEST, "Current Password is incorrect"),//contraseña incorrecta
    NEW_PASSWORD_DOES_NOT_MATCH(301, HttpStatus.BAD_REQUEST, "The password does not mach"),//contraseña no conincide
    ACCOUNT_LOCKED(302, HttpStatus.FORBIDDEN, "User account is locked"), //la cuenta de usuario bloqueada
    ACCOUNT_DISABLE(303, HttpStatus.FORBIDDEN, "User account is locked"), //la cuenta de usuario desabilitada
    BAD_CREDENTIALS(304, HttpStatus.FORBIDDEN, "Login and/ or password is incorrect"); //la cuenta de usuario icorrecta

    private final int code;
    private final HttpStatus httpStatus;
    private final String description;
    }
