package com.ryanstewart.ElasticSpringBootTest;

import lombok.Data;

import java.util.List;

@Data
public class ProfileDocument {

    private String id;
    private String firstName;
    private String lastName;
    private List<String> emails;

}
