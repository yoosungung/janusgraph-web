package org.janusgraph.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.janusgraph.web.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/client")
public class ClientController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientController.class);
    private static final String MIXED_INDEX_CONFIG_NAME = "jgex";

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/submit")
    public List<String> submit(@RequestBody String body) {
        return clientService.submit(body);
    }

    @PostMapping("/schema")
    public List<String> makeSchema(@RequestBody JsonNode body) {
        if(body.isEmpty() || !body.isArray()) {
            return new ArrayList<>() {{
                add("Body format error :[ {\"schema\": \"PropertyKey|VertexLabel|EdgeLabel|Index\", \"name\": \"...\", \"dataType\": \"for PropertyKey\", \"multiplicity\": \"for EdgeLabel\", \"indexFor\": \"Vertex|Edge\", \"keys\": [\"keyName\", ...] }, {...} ]");
            }};
        } else {
            final String s = clientService.getSchemaCommand(body);
            return clientService.submit(s);
        }
    }

    @PostMapping("/schema/init")
    public List<String> initSchema() {
        final JsonNode body = clientService.initSchema();
        final String s = clientService.getSchemaCommand(body);
        return clientService.submit(s);
    }
}
