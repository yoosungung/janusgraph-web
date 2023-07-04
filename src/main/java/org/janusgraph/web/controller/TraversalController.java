package org.janusgraph.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.janusgraph.web.service.TraversalService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/traversal")
public class TraversalController {
    private final TraversalService traversalService;

    public TraversalController(TraversalService traversalService) {
        this.traversalService = traversalService;
    }

    @PostMapping("/submit")
    public JsonNode submit(@RequestBody JsonNode body) {
        if(body.has("query")) {
            return null;
        } else {
            throw new RuntimeException("Body format error :{ \"query\": \"...\" }");
        }
    }
}
