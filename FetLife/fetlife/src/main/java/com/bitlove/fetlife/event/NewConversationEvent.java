package com.bitlove.fetlife.event;

public class NewConversationEvent {

    private final String conversationId;

    public NewConversationEvent(String conversationId) {
       this.conversationId = conversationId;
    }

    public String getConversationId() {
        return conversationId;
    }
}
