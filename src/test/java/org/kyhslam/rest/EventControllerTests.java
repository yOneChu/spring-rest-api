package org.kyhslam.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kyhslam.rest.common.RestDocsConfiguration;
import org.kyhslam.rest.common.TestDescription;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.hypermedia.HypermediaDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EventRepository eventRepository;

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
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE,MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("free").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("offline").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("_links.self").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("_links.query-events").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("_links.update-event").exists())
                .andDo(MockMvcRestDocumentation.document("create-event",
                        HypermediaDocumentation.links(
                                HypermediaDocumentation.linkWithRel("self").description("link to self"),
                                HypermediaDocumentation.linkWithRel("query-events").description("link to query"),
                                HypermediaDocumentation.linkWithRel("update-event").description("link to update")
                        ),
                        HeaderDocumentation.requestHeaders(
                                HeaderDocumentation.headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                HeaderDocumentation.headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
                        )
                ))
                ;

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


    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvent() throws Exception {

        // Given
        IntStream.range(0,30).forEach(i -> {
            this.generateEvent(i);
        });

        // When
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/events")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "name,DESC")
                    )
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("page").exists());
    }

    public void generateEvent(int index){
        Event event = Event.builder()
                .name("event" + index)
                .description("test event")
                .build();

        this.eventRepository.save(event);
    }
}
