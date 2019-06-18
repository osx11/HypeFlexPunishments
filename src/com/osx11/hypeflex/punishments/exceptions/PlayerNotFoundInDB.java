package com.osx11.hypeflex.punishments.exceptions;

import com.osx11.hypeflex.punishments.data.MessagesData;

public class PlayerNotFoundInDB extends Exception {

    public PlayerNotFoundInDB(final String nick) {
        super(MessagesData.getMSG_PlayerNotFound(nick));
    }

}
