package com.github.kornilova_l.formal_da.implementation.BMM;

import com.github.kornilova_l.formal_da.vertex.Message;

final class BmmMessage extends Message {
    private final BmmMessageContent messageContent;

    BmmMessage(BmmMessageContent messageContent) {
        this.messageContent = messageContent;
    }

    BmmMessageContent getMessageContent() {
        return messageContent;
    }

    public enum BmmMessageContent {
        PROPOSAL,
        ACCEPT,
        MATCHED
    }
}
