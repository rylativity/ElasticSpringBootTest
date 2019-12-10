package com.ryanstewart.ElasticSpringBootTest.service;

import com.ryanstewart.ElasticSpringBootTest.ProfileDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public List<ProfileDocument> findByQuery(String searchTerm, String fieldName) throws Exception {

        SearchRequest searchRequest = new SearchRequest("profiles");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.queryStringQuery(fieldName + ":" + searchTerm));

        searchRequest.source(searchSourceBuilder);
        searchRequest.types("_doc");

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();

        SearchHit[] searchHits = hits.getHits();

        List<ProfileDocument> searchReturn = new ArrayList<>();

        for (SearchHit hit : searchHits) {

            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            ProfileDocument profileDocument = objectMapper.convertValue(sourceAsMap, ProfileDocument.class);
            searchReturn.add(profileDocument);
        }

        return searchReturn;

    }
}
