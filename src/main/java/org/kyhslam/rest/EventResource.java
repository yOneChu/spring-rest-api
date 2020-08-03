package org.kyhslam.rest;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

public class EventResource extends Resource<Event> {

    public EventResource(Event event, Link... links) {
        super(event, links);
        add(ControllerLinkBuilder.linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }
}
