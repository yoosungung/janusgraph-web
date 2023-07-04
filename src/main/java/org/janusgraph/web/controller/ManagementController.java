package org.janusgraph.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.janusgraph.web.service.ManagementService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/management")
public class ManagementController {
    private final ManagementService managementService;

    public ManagementController(ManagementService managementService) {
        this.managementService = managementService;
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
