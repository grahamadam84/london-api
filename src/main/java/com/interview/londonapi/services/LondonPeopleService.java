package com.interview.londonapi.services;

import com.grum.geocalc.Coordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;
import com.interview.londonapi.models.Location;
import com.interview.londonapi.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Service
public class LondonPeopleService {

    private RestService restService;
    private static final String BASE_URL = "https://bpdts-test-app-v3.herokuapp.com/";
    private static final String USERS_URL = BASE_URL + "users";
    private static final String CITY_URL = BASE_URL + "city/";

    @Autowired
    public LondonPeopleService(RestService restService) {
        this.restService = restService;
    }

    @Async
    public CompletableFuture<Person[]> getUsersByCity(String city) {
        return CompletableFuture.completedFuture(
                restService.get(CITY_URL + city + "/users")
        );
    }

    @Async
    public CompletableFuture<Person[]> getUsersWithinRadiusOf(Location location, double radius) {
        Person[] people = Arrays.stream(restService.get(USERS_URL)).filter(
                p -> isPersonWithinRadiusOf(p, radius, location)
        ).toArray(Person[]::new);

        return CompletableFuture.completedFuture(people);
    }

    private boolean isPersonWithinRadiusOf(Person person, double radius, Location location) {
        Location personLocation = new Location(person.getLongitude(), person.getLatitude());
        double distance = distance(location, personLocation);
        return distance <= radius;
    }

    private double distance(Location from, Location to) {
        Point pointFrom = Point.at(
                Coordinate.fromDegrees(from.getLatitude()), Coordinate.fromDegrees(from.getLongitude())
        );

        Point pointTo = Point.at(
                Coordinate.fromDegrees(to.getLatitude()), Coordinate.fromDegrees(to.getLongitude())
        );

        return EarthCalc.vincentyDistance(pointFrom, pointTo);
    }

}
