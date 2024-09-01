package com.dusseldorf.email;

import lombok.Getter;

/**
 * plantilla del email
 */
@Getter
public enum EmailTemplateName {
    //activar cuenta
    ACTIVATE_ACCOUNT("activate_account");

    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }
}
