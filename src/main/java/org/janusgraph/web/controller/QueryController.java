package org.janusgraph.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.janusgraph.web.service.QueryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/query")
public class QueryController {
    private final QueryService graphService;

    public QueryController(QueryService graphService) {
        this.graphService = graphService;
    }

    @PostMapping("/submit")
    public JsonNode submit(@RequestBody JsonNode body) {
        if(body.has("query")) {
            return graphService.submit(body.get("query").asText());
        } else {
            throw new RuntimeException("Body format error :{ \"query\": \"...\" }");
        }
    }
}
