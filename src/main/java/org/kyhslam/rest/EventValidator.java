package org.kyhslam.rest;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

//비즈니스 로직 검증
@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors){

        //금액 검증
        if(eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() != 0){
            errors.rejectValue("basePrice", "wrongValue", "basePrice is wrong.");
            errors.rejectValue("maxPrice", "wrongValue", "basePrice is wrong.");
        }

        //날짜 검증
        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if(endEventDateTime.isBefore(eventDto.getBeginEventDateTime())){
            errors.rejectValue("endEventDateTime", "wrongValue");
        }

        // TODO BeginEventDateTime
        // TODO CloseEnrollmentDateTime

    }
}
