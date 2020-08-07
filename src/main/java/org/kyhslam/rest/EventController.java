package org.kyhslam.rest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator){
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }


    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }

        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class); // Dto를 event로 변환

        event.update(); // 비즈니스 로직 적용

        Event newEvent = eventRepository.save(event);

        ControllerLinkBuilder selfLinkBuilder = ControllerLinkBuilder.linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = ControllerLinkBuilder.linkTo(EventController.class).slash(newEvent.getId()).toUri();

        EventResource eventResource = new EventResource(event);
        eventResource.add(ControllerLinkBuilder.linkTo(EventController.class).withRel("query-events"));
        //eventResource.add(selfLinkBuilder.withSelfRel());
        eventResource.add(selfLinkBuilder.withRel("update-event"));

        return ResponseEntity.created(createdUri).body(eventResource);
    }


    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler){

        Page<Event> page = this.eventRepository.findAll(pageable);
        PagedResources<Resource<Event>> pagedResources = assembler.toResource(page);

        // profile link 추가

        return ResponseEntity.ok(pagedResources);
    }


    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id){
        Optional<Event> optionalEvent = this.eventRepository.findById(id);

        if(optionalEvent.isPresent() == false){
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);

        //profile link 추가
        eventResource.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }





}
