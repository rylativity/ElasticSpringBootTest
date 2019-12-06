package com.ryanstewart.ElasticSpringBootTest.service;

import com.ryanstewart.ElasticSpringBootTest.ProfileDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ProfileService {

    private RestHighLevelClient client;

    private ObjectMapper objectMapper;

    @Autowired
    public ProfileService(RestHighLevelClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public String createProfile(ProfileDocument document) throws Exception {

        UUID uuid = UUID.randomUUID();
        document.setId(uuid.toString());

        Map<String, Object> documentMapper = objectMapper.convertValue(document, Map.class);

        IndexRequest indexRequest = new IndexRequest("profiles", "_doc", document.getId()).source(documentMapper);

       IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);

       return indexResponse.getResult().name();
    }

    public ProfileDocument findById(String id) throws Exception {

        GetRequest getRequest = new GetRequest("profiles", "_doc", id);

        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        Map<String, Object> resultMap = getResponse.getSource();

        return objectMapper.convertValue(resultMap, ProfileDocument.class);
    }
}
