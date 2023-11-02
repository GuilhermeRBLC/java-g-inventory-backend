package com.guilhermerblc.inventory.exceptions;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@NoArgsConstructor
@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason = "IDs must be the same in path and body.")
public class IdentificationNotEqualsException extends RuntimeException {

    public IdentificationNotEqualsException(String message) {
        super(message);
    }

}
