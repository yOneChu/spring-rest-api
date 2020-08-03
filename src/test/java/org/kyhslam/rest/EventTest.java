package org.kyhslam.rest;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class EventTest {

    //@Test
    public void builder(){
        Event event = Event.builder()
                .name("Inflearn Spring REST API")
                .description("REST API development with Spring")
                .build();

        Assertions.assertThat(event).isNotNull();
    }

    public void javaBean(){
        // Given
        String name = "Event";
        String description = "Spring";

        // When
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        // Then
        Assertions.assertThat(event.getName()).isEqualTo(name);
        Assertions.assertThat(event.getDescription()).isEqualTo(description);
    }

    @Test
    @Parameters(method = "paramsForTestFree")
    public void testFree(int basePrice, int maxPrice, boolean isFree){

        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        // When
        event.update();

        // Then
        Assertions.assertThat(event.isFree()).isEqualTo(isFree);
    }

    private Object[] paramsForTestFree(){
        return new Object[] {
                new Object[] {0,0,true},
                new Object[] {0,0,true},
                new Object[] {0,0,true}
        };
    }

    @Test
    @Parameters(method = "paramsForTestOffline")
    public void testOffline(String location, boolean isOffline){
        // Given
        Event event = Event.builder()
                .location(location.trim())
                .build();

        // When
        event.update();

        // Then
        Assertions.assertThat(event.isOffline()).isEqualTo(isOffline);
    }

    private Object[] paramsForTestOffline(){
        return new Object[] {
                new Object[] {"강남",true},
                new Object[] {null,false},
                new Object[] {"  ",false}
        };
    }

}
