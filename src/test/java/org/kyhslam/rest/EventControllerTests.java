package org.kyhslam.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kyhslam.rest.common.TestDescription;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {

        Event event = Event.builder()
                .name("Spring")
                .description("REST API Development")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .beginEventDateTime(LocalDateTime.of(2018,11,25,14,21))
                .endEventDateTime(LocalDateTime.of(2018,11,26,14,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 d2 팩토리")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/events/")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").exists())
                .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.LOCATION))
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE,MediaTypes.HAL_JSON_UTF8_VALUE));

    }



    @Test
    @TestDescription("입력값이 비어있는 경우 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_input() throws Exception {

        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    //들어온 값이 이상할 경우
    @Test
    @TestDescription("입력값이잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_input() throws Exception {

        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .beginEventDateTime(LocalDateTime.of(2018,11,25,14,21))
                .endEventDateTime(LocalDateTime.of(2018,11,26,14,21))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 d2 팩토리")
                .build();

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].objectName").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].defaultMessage").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].code").exists())
                ;

    }

}
