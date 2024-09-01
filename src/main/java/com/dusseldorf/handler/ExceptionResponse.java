package com.dusseldorf.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY) // PARA INCLUIR SOLO LOS ATRIBUTOS NO VACIOS
public class ExceptionResponse {

    private Integer businessErrorCode;    //CODIGO DE ERROR-> PARA CADA EXCEPCION
    private String businessErrorDescription;
    private String error; //para que defina cualquier atributo que encuentre
    private Set<String> validationError; // contenga una lista de validacion
    private Map<String, String> errors;

 }
