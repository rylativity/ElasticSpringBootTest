package com.ryanstewart.ElasticSpringBootTest.controller;

import com.ryanstewart.ElasticSpringBootTest.ProfileDocument;
import com.ryanstewart.ElasticSpringBootTest.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private ProfileService service;

    @Autowired
    public ProfileController(ProfileService service) {

        this.service = service;
    }

    @PostMapping
    public ResponseEntity createProfile(
            @RequestBody ProfileDocument document) throws Exception {

        return new ResponseEntity(service.createProfile(document), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ProfileDocument findById(@PathVariable String id) throws Exception {

        return service.findById(id);
    }

    @GetMapping("/search/{searchTerm}")
    public ArrayList<ProfileDocument> findByQuery(@PathVariable String searchTerm) throws Exception {

        return service.findByQuery(searchTerm);
    }

}
