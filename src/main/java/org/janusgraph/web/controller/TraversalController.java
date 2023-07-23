package org.janusgraph.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.janusgraph.web.service.TraversalService;
import org.janusgraph.web.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/traversal")
public class TraversalController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraversalController.class);

    private final TraversalService traversalService;

    public TraversalController(ObjectMapper objectMapper, TraversalService traversalService) {
        this.traversalService = traversalService;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleIOException(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @GetMapping("/schema")
    public JsonNode getSchema() throws Exception {
        return JsonUtil.toJson(traversalService.getAllSchema());
    }

    @GetMapping("/schema/vertex/{label}")
    public JsonNode schemaVertexByLabel(@PathVariable String label) throws Exception {
        return JsonUtil.toJson(traversalService.getVertexSchema(label));
    }

    @PostMapping("/vertex")
    public JsonNode makeVertex(@RequestBody JsonNode body) throws Exception {
        if(body.has("label")) {
            return JsonUtil.toJson(traversalService.makeVertex(body));
        } else {
            throw new Exception("Body format error : { label: <person|organization|writing|subject>, ... }");
        }
    }

    @GetMapping("/vertex")
    public JsonNode findVertexByIdList(@RequestParam(name = "ids") String ids) throws Exception {
        if(ids.isEmpty()) {
            throw new Exception("parameter format error :ids=1,2,3");
        } else {
            List<Long> idList = new ArrayList<>();
            for(String id: ids.split(",")) {
                idList.add(Long.parseLong(id));
            }
            return JsonUtil.toArrayMap(traversalService.valyeVertexByIdlist(idList));
        }
    }

    @GetMapping("/vertex/{label}")
    public JsonNode findVertexByLabel(@PathVariable String label, @RequestParam Map<String, String> queryParams) throws Exception {
        return JsonUtil.toArrayVertex(traversalService.findVertexByParams(label, queryParams));
    }

    @GetMapping("/vertex/{id}/value")
    public JsonNode valuesVertexById(@PathVariable String id) throws Exception {
        return JsonUtil.toJson(traversalService.valuesVertexById(id));
    }

    @GetMapping("/schema/edge/{label}")
    public JsonNode schemaEdgeByLabel(@PathVariable String label) throws Exception {
        return JsonUtil.toJson(traversalService.getEdgeSchema(label));
    }

    @PostMapping("/edge")
    public JsonNode makeEdge(@RequestBody JsonNode body) throws Exception {
        if(body.has("label")) {
            return JsonUtil.toJson(traversalService.makeEdge(body));
        } else {
            throw new Exception("Body format error : { label: <create|parent|assessment|belong>, ... }");
        }
    }

    @GetMapping("/edge/{id}/value")
    public JsonNode valuesEdgeById(@PathVariable String id) throws Exception {
        return JsonUtil.toJson(traversalService.valuesEdgeById(id));
    }

    @GetMapping("/edge/{id}/out")
    public JsonNode findOutEdgeByVertexId(@PathVariable String id) throws Exception {
        return JsonUtil.toArrayEdge(traversalService.findOutEdgeByVertexId(id));
    }

    @GetMapping("/edge/{id}/in")
    public JsonNode findInEdgeByVertexId(@PathVariable String id) throws Exception {
        return JsonUtil.toArrayEdge(traversalService.findInEdgeByVertexId(id));
    }
}
