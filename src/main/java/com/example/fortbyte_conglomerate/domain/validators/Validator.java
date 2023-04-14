package com.example.fortbyte_conglomerate.domain.validators;

import com.example.fortbyte_conglomerate.domain.validators.strategies.ValidateBehaviour;

public class Validator {
    private final ValidateBehaviour validateBehaviour;

    public Validator(ValidateBehaviour validateBehaviour) {
        this.validateBehaviour = validateBehaviour;
    }

    /**
     * Validates the object using a specific behaviour(strategy)
     * @param o object to be validated
     */
    public void validate(Object o){
        validateBehaviour.validate(o);
    }
}
