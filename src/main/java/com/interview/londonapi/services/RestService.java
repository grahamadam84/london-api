package com.interview.londonapi.services;

import com.interview.londonapi.models.Person;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestService {

    private final RestTemplate restTemplate;

    public RestService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public Person[] get(String url) {
        return this.restTemplate.getForObject(url, Person[].class);
    }


}
