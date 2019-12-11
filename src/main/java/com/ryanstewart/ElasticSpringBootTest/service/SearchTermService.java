package com.ryanstewart.ElasticSpringBootTest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryanstewart.ElasticSpringBootTest.repository.SearchTermDocument;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
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

@Service
@Slf4j
public class SearchTermService {

    private RestHighLevelClient client;

    private ObjectMapper objectMapper;

    @Autowired
    public SearchTermService(RestHighLevelClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public SearchTermDocument findById(String id) throws Exception {

        GetRequest getRequest = new GetRequest("penguinterms", "_doc", id);

        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        Map<String, Object> resultMap = getResponse.getSource();

        return objectMapper.convertValue(resultMap, SearchTermDocument.class);
    }

    public List<SearchTermDocument> findByTerms(String auditId, List<String> terms, String from, String size) throws Exception {

        SearchRequest searchRequest = new SearchRequest("penguinterms");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        String queryString;

        int numTerms = terms.size();
        if (numTerms == 0) {
            queryString = "auditId:" + auditId;
        } else if (numTerms ==1) {
            queryString = "term:"+ terms.get(0) + " AND " + "auditId:" + auditId;
        } else {
            String baseQueryString = new String("term:"+ terms.get(0));
            StringBuilder sb = new StringBuilder();
            sb.append(baseQueryString);
            for (int i=1; i < numTerms; i++) {
                System.out.println(terms.get(i));
                sb.append(" AND " + "term:" + terms.get(i));
            }
            sb.append(" AND " + "auditId:" + auditId);
            queryString = sb.toString();
        }
        System.out.println(queryString);

        searchSourceBuilder.query(QueryBuilders.queryStringQuery(queryString));

        String[] includeFields = new String[] {"hbaseTable","recordIdentifier", "auditId"};
        String[] excludeFields = new String[] {"term","termCategory","firstDate","lastDate"};
        searchSourceBuilder.fetchSource(includeFields, excludeFields)
                .from(Integer.parseInt(from))
                .size(Integer.parseInt(size));

        searchRequest.source(searchSourceBuilder);
        searchRequest.types("_doc");

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        SearchHits hits = searchResponse.getHits();

        SearchHit[] searchHits = hits.getHits();

        List<SearchTermDocument> searchReturn = new ArrayList<>();

        for (SearchHit hit : searchHits) {

            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            SearchTermDocument searchTermDocument = objectMapper.convertValue(sourceAsMap, SearchTermDocument.class);
            searchReturn.add(searchTermDocument);
        }

        return searchReturn;
    }
}
