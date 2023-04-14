package com.example.fortbyte_conglomerate.domain.validators.strategies;

import com.example.fortbyte_conglomerate.domain.Friendship;
import com.example.fortbyte_conglomerate.exceptions.ValidationException;

public class ValidateFriendship implements ValidateBehaviour{
    public void validate(Object Obj) throws ValidationException {
        Friendship friendship = (Friendship)Obj;
        String errMsg="";
        if (friendship.getId() == null)
            errMsg+="Id error ";
        if (friendship.getUserIds() == null)
            errMsg+="friendship set error ";
        if (!errMsg.equals(""))
            throw new ValidationException(errMsg);
    }
}
