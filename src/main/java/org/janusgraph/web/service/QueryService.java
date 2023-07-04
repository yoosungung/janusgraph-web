package org.janusgraph.web.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.ResultSet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QueryService {
    private final ObjectMapper objectMapper;
    private final Client client;

    public QueryService(Client client) throws Exception {
        this.client = client;
        this.objectMapper = new ObjectMapper();
    }

    public JsonNode submit(String query) {
        ResultSet results = client.submit(query);
        List<String> resStr = results.stream()
                .map(Result::toString)
                .collect(Collectors.toList());
        return objectMapper.valueToTree(resStr);
    }
}
