package com.bitlove.fetchat.event;

public class ServiceCallStartedEvent {

    private String serviceCallAction;

    public ServiceCallStartedEvent(String serviceCallAction) {
        this.serviceCallAction = serviceCallAction;
    }

    public String getServiceCallAction() {
        return serviceCallAction;
    }
}
