package com.dusseldorf.handler;

import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handlerException(LockedException exp){
        //excepcion Bloqueada
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                       ExceptionResponse.builder()
                               .businessErrorCode(BusinessError.ACCOUNT_LOCKED.getCode())
                               .businessErrorDescription(BusinessError.ACCOUNT_LOCKED.getDescription())
                               .error(exp.getMessage())
                               .build()
                );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handlerException(DisabledException exp){
        //excepcion Desabilitada
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                       ExceptionResponse.builder()
                               .businessErrorCode(BusinessError.ACCOUNT_DISABLE.getCode())
                               .businessErrorDescription(BusinessError.ACCOUNT_DISABLE.getDescription())
                               .error(exp.getMessage())
                               .build()
                );
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handlerException(BadCredentialsException exp){
        //excepcion de credenciales malas
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                       ExceptionResponse.builder()
                               .businessErrorCode(BusinessError.BAD_CREDENTIALS.getCode())
                               .businessErrorDescription(BusinessError.BAD_CREDENTIALS.getDescription())
                               .error(BusinessError.BAD_CREDENTIALS.getDescription())
                               .build()
                );
    }
    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handlerException(MessagingException exp){
        //excepcion cuando no se puede enviar un correo electronico
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                       ExceptionResponse.builder()
                               .error(exp.getMessage())
                               .build()
                );
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handlerException(MethodArgumentNotValidException exp){
        //excepcion cuando envia datos no validos
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors().forEach(error -> {
            var errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) // sera una mala solicitud
                .body(
                       ExceptionResponse.builder()
                               .validationError(errors)
                               .build()
                );
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handlerException(Exception exp){
        //en el caso en el que obtenga cualquier tipo de exception
        exp.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                       ExceptionResponse.builder()
                               .businessErrorDescription("Internal error, contact the Admin")
                               .error(exp.getMessage())
                               .build()
                );
    }
}
