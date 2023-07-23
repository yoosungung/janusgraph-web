package org.janusgraph.web.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.web.controller.TraversalController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class JsonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraversalController.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode toJson(String str) throws JsonProcessingException {
        return objectMapper.readTree(str);
    }

    public static JsonNode toJson(Vertex vertex) {
        ObjectNode result = objectMapper.createObjectNode();
        result.put("id", (Long) vertex.id());
        result.put("label", vertex.label());
        return result;
    }

    public static JsonNode toArrayEdge(List<Edge> edgeList) {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for(Edge edge : edgeList) {
            arrayNode.add(toJson(edge));
        }
        return arrayNode;
    }

    public static JsonNode toJson(Edge edge) {
        ObjectNode result = objectMapper.createObjectNode();
        result.put("id", edge.id().toString());
        result.put("label", edge.label());
        result.put("inV", (Long) edge.inVertex().id());
        result.put("outV", (Long) edge.outVertex().id());
        return result;
    }

    public static JsonNode toJson(Map<Object, Object> map) {
        return objectMapper.convertValue(map, JsonNode.class);
    }

    public static JsonNode toArrayMap(List<Map<Object, Object>> maplist) {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for(Map<Object, Object> map : maplist) {
            arrayNode.add(toJson(map));
        }
        return arrayNode;
    }

    public static JsonNode toArrayVertex(List<Vertex> vertexList) {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for(Vertex vertex : vertexList) {
            arrayNode.add(toJson(vertex));
        }
        return arrayNode;
    }
}
