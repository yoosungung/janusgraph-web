package org.janusgraph.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.web.service.TraversalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/traversal")
public class TraversalController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraversalController.class);

    private final ObjectMapper objectMapper;
    private final TraversalService traversalService;

    public TraversalController(ObjectMapper objectMapper, TraversalService traversalService) {
        this.objectMapper = objectMapper;
        this.traversalService = traversalService;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleIOException(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @GetMapping("/schema")
    public JsonNode getSchema() throws Exception {
        return toJson(traversalService.getAllSchema());
    }

    @GetMapping("/schema/vertex/{label}")
    public JsonNode schemaVertexByLabel(@PathVariable String label) throws Exception {
        return toJson(traversalService.getVertexSchema(label));
    }

    @PostMapping("/vertex")
    public JsonNode makeVertex(@RequestBody JsonNode body) throws Exception {
        if(body.has("label")) {
            return toJson(traversalService.makeVertex(body));
        } else {
            throw new Exception("Body format error : { label: <person|organization|writing|subject>, ... }");
        }
    }

    @GetMapping("/vertex/{label}")
    public JsonNode findVertexByLabel(@PathVariable String label, @RequestParam Map<String, String> queryParams) {
        return toJson(traversalService.findVertexByParams(label, queryParams));
    }

    @GetMapping("/vertex/{id}/value")
    public JsonNode valuesVertexById(@PathVariable String id) {
        return toJson(traversalService.valuesVertexById(id));
    }

    @GetMapping("/schema/edge/{label}")
    public JsonNode schemaEdgeByLabel(@PathVariable String label) throws Exception {
        return toJson(traversalService.getEdgeSchema(label));
    }

    @PostMapping("/edge")
    public JsonNode makeEdge(@RequestBody JsonNode body) throws Exception {
        if(body.has("label")) {
            return toJson(traversalService.makeEdge(body));
        } else {
            throw new Exception("Body format error : { label: <create|parent|assessment|belong>, ... }");
        }
    }

    @GetMapping("/edge/{id}/value")
    public JsonNode valuesEdgeById(@PathVariable String id) {
        return toJson(traversalService.valuesEdgeById(id));
    }

    @GetMapping("/edge/{id}/out")
    public JsonNode findOutEdgeByVertexId(@PathVariable String id) {
        return toJson(traversalService.findOutEdgeByVertexId(id));
    }

    @GetMapping("/edge/{id}/in")
    public JsonNode findInEdgeByVertexId(@PathVariable String id) {
        return toJson(traversalService.findInEdgeByVertexId(id));
    }

    public JsonNode toJson(String str) throws JsonProcessingException {
        return objectMapper.readTree(str);
    }
    public JsonNode toJson(Vertex vertex) {
        ObjectNode result = objectMapper.createObjectNode();
        result.put("id", (Long) vertex.id());
        result.put("label", vertex.label());
        return result;
    }
    public JsonNode toJson(List<Edge> edgeList) {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for(Edge edge : edgeList) {
            arrayNode.add(toJson(edge));
        }
        return arrayNode;
    }
    private JsonNode toJson(Edge edge) {
        ObjectNode result = objectMapper.createObjectNode();
        result.put("id", edge.id().toString());
        result.put("label", edge.label());
        result.put("inV", (Long) edge.inVertex().id());
        result.put("outV", (Long) edge.outVertex().id());
        return result;
    }
    public JsonNode toJson(Map<Object, Object> map) {
        return objectMapper.convertValue(map, JsonNode.class);
    }
}
