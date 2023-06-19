package org.smartregister.chw.domain;

import org.smartregister.domain.Client;
import org.smartregister.domain.Event;

/**
 * Author issyzac on 15/06/2023
 */
public class GroupEventClient {

    private Event event;
    private Client client;

    public GroupEventClient(Client client, Event event) {
        this.client = client;
        this.event = event;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

}
