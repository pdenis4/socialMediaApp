package com.example.fortbyte_conglomerate.domain.validators.strategies;

import com.example.fortbyte_conglomerate.domain.Account;
import com.example.fortbyte_conglomerate.exceptions.ValidationException;

public class ValidateAccount implements ValidateBehaviour {
    @Override
    public void validate(Object obj) throws ValidationException {
        Account account = (Account) obj;
        if (!account.getMail().contains("@"))
            throw new ValidationException("Mail needs to contain @");
        if(account.getPassword().length() < 5)
            throw new ValidationException("Password needs to be at least 5 characters long");
    }
}
