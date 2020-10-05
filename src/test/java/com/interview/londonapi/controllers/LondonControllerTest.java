package com.interview.londonapi.controllers;

import com.interview.londonapi.models.Person;
import com.interview.londonapi.services.LondonPeopleService;
import com.interview.londonapi.services.RestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestClientException;

import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LondonControllerTest {

    private LondonController sut;
    private static final String CITY = "London";
    private static final String BASE_URL = "https://bpdts-test-app-v3.herokuapp.com/";
    private static final String USERS_URL = BASE_URL + "users";
    private static final String CITY_URL = BASE_URL + "city/" + CITY + "/users";
    private static final Person cityPerson = new Person();
    private static final Person radiusPersonWithin = new Person(1, null, null, null, null, 51.5489435, 0.3860497);
    private static final Person radiusPersonOutside = new Person(2, null, null, null, null, -6.5115909, 105.652983);
    private static final Person[] cityPeople = { cityPerson };
    private static final Person[] radiusPeople = {radiusPersonWithin, radiusPersonOutside };
    private static final Person[] expectedPeople = new Person[] { cityPerson, radiusPersonWithin};

    @Mock
    RestService restService;

    @BeforeEach
    public void setup() {
        initMocks(this);
        sut = new LondonController(new LondonPeopleService(restService));
        when(restService.get(CITY_URL)).thenReturn(cityPeople);
        when(restService.get(USERS_URL)).thenReturn(radiusPeople);
    }

    @Test
    public void getLondonPeopleTest() throws ExecutionException, InterruptedException {
        Person[] people = sut.getLondonPeople();
        assertThat(people, is(expectedPeople));
    }

    @Test
    public void getLondonPeopleTest_WhenExceptionGettingUsers_ThenExceptionBubblesUp() {
        when(restService.get(USERS_URL)).thenThrow(new RestClientException("TEST"));
        Exception ex = assertThrows(RestClientException.class, () -> {
            sut.getLondonPeople();
        });
        assertThat(ex.getMessage(), is("TEST"));
    }

    @Test
    public void getLondonPeopleTest_WhenExceptionGettingCity_ThenExceptionBubblesUp() {
        when(restService.get(CITY_URL)).thenThrow(new RestClientException("TEST"));
        Exception ex = assertThrows(RestClientException.class, () -> {
            sut.getLondonPeople();
        });
        assertThat(ex.getMessage(), is("TEST"));
    }

}