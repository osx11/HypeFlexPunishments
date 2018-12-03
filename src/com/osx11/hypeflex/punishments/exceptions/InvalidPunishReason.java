package com.osx11.hypeflex.punishments.exceptions;

import com.osx11.hypeflex.punishments.data.MessagesData;

public class InvalidPunishReason extends Exception {

    public InvalidPunishReason() {
        super(MessagesData.getMSG_InvalidPunishReason());
    }

}
