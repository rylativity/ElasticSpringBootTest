package com.ryanstewart.ElasticSpringBootTest.controller;

import com.ryanstewart.ElasticSpringBootTest.repository.SearchTermDocument;
import com.ryanstewart.ElasticSpringBootTest.service.SearchTermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchTermController {

    private SearchTermService service;

    @Autowired
    public SearchTermController(SearchTermService service) {

        this.service = service;
    }

    @PostMapping("/{auditId}/{from}:{size}")
    public List<SearchTermDocument> findByTerms(@PathVariable String auditId, @RequestBody List<String> terms, @PathVariable String from, @PathVariable String size) throws Exception {

        return service.findByTerms(auditId, terms, from, size);
    }


}
