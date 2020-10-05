package com.interview.londonapi.controllers;

import com.interview.londonapi.models.Location;
import com.interview.londonapi.models.Person;
import com.interview.londonapi.services.LondonPeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@RestController
public class LondonController {
    public static final String CITY = "London";
    public static final Location LONDON_LOCATION = new Location(-0.118092, 51.509865);
    public static final double RADIUS_METRES = 60 * 1609.34;

    LondonPeopleService londonPeopleService;

    @Autowired
    public LondonController(LondonPeopleService londonPeopleService) {
        this.londonPeopleService = londonPeopleService;
    }

    @GetMapping("/")
    public Person[] getLondonPeople() throws ExecutionException, InterruptedException {
        CompletableFuture<Person[]> resultByCity = londonPeopleService.getUsersByCity(CITY);
        CompletableFuture<Person[]> resultByRadius = londonPeopleService.getUsersWithinRadiusOf(
                LONDON_LOCATION, RADIUS_METRES
        );

        CompletableFuture.allOf(resultByCity, resultByRadius).join();

        return Stream.concat(
                Stream.of(resultByCity.get()), Stream.of(resultByRadius.get())
        ).toArray(Person[]::new);
    }

}

