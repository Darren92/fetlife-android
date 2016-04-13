package com.bitlove.fetchat.event;

public class ServiceCallFailedEvent {

    private String serviceCallAction;

    public ServiceCallFailedEvent(String serviceCallAction) {
        this.serviceCallAction = serviceCallAction;
    }

    public String getServiceCallAction() {
        return serviceCallAction;
    }

}
