package com.bitlove.fetlife.event;

public class MessageSendFailedEvent {
    private final String conversationId;

    public MessageSendFailedEvent(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationId() {
        return conversationId;
    }
}
