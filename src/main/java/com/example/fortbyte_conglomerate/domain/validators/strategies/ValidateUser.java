package com.example.fortbyte_conglomerate.domain.validators.strategies;

import com.example.fortbyte_conglomerate.domain.User;
import com.example.fortbyte_conglomerate.exceptions.ValidationException;

public class ValidateUser implements ValidateBehaviour{
    @Override
    public void validate(Object userObj) throws ValidationException {
        User user = (User)userObj;
        String errMsg="";
        if (user.getId() == null)
            errMsg+="Id error ";
        if (user.getFirstName() == null || "".equals(user.getFirstName()))
            errMsg+="first name error ";
        if (user.getLastName() == null || "".equals(user.getLastName()))
            errMsg+="last name error ";
        if (user.getFriendsIds() == null)
            errMsg+="friends error ";
        if (!errMsg.equals(""))
            throw new ValidationException(errMsg);
    }
}
