package com.osx11.hypeflex.punishments.exceptions;

import com.osx11.hypeflex.punishments.data.MessagesData;

public class InvalidTimeIdentifier extends Exception {

    public InvalidTimeIdentifier(String value) {
        super(MessagesData.getMSG_InvalidTimeIndentifier(value));
    }

}
