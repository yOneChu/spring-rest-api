package org.kyhslam.rest.common;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.validation.Errors;

public class ErrorsResource extends Resource<Errors> {
    public ErrorsResource(Errors content, Link... links){
        super(content, links);
        //
    }
}
