package com.ryanstewart.ElasticSpringBootTest.repository;

import lombok.Data;

@Data
public class SearchTermDocument {

    private String hbaseTable;
    private String recordIdentifier;
    private String auditId;

}
