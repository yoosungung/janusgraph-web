package org.janusgraph.web.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientService.class);

    private final Client client;
    private final ObjectMapper mapper;

    public ClientService(Client client) {
        this.client = client;
        this.mapper = new ObjectMapper();
    }

    public List<String> submit(String query) {
        ResultSet results = client.submit(query);
        return results.stream()
                .map(Result::toString)
                .collect(Collectors.toList());
    }

    public String getSchemaCommand(JsonNode query) {
        final StringBuilder s = new StringBuilder();
        s.append("JanusGraphManagement management = graph.openManagement(); ");
        for (JsonNode jsonNode : query) {
            switch (jsonNode.get("schema").asText()) {
                case "PropertyKey":
                    s.append("management.makePropertyKey(\"").append(jsonNode.get("name").asText()).append("\").dataType(").append(jsonNode.get("dataType").asText()).append(".class).make(); ");
                    break;
                case "VertexLabel":
                    s.append("management.makeVertexLabel(\"").append(jsonNode.get("name").asText()).append("\").make(); ");
                    break;
                case "EdgeLabel":
                    s.append("management.makeEdgeLabel(\"").append(jsonNode.get("name").asText()).append("\")");
                    if (jsonNode.has("multiplicity")) {
                        s.append(".multiplicity(Multiplicity.").append(jsonNode.get("multiplicity").asText()).append(")");
                    }
                    if (jsonNode.has("signature")) {
                        s.append(".signature(").append(jsonNode.get("signature").asText()).append(")");
                    }
                    s.append(".make(); ");
                    break;
                case "Index":
                    if (jsonNode.has("keys")) {
                        s.append("management.buildIndex(\"").append(jsonNode.get("name").asText()).append("\", ").append(jsonNode.get("indexFor").asText("Vertex")).append(".class)");
                        JsonNode keys = jsonNode.get("keys");
                        for (JsonNode key : keys) {
                            s.append(".addKey(management.getPropertyKey(\"").append(key.asText()).append("\"))");
                        }
                        s.append(".buildCompositeIndex(); ");
                    }
//                        s.append("management.buildIndex(\"vAge\", Vertex.class).addKey(age).buildMixedIndex(\"").append(MIXED_INDEX_CONFIG_NAME).append("\"); ");
//                        s.append("management.buildIndex(\"eReasonPlace\", Edge.class).addKey(reason).addKey(place).buildMixedIndex(\"").append(MIXED_INDEX_CONFIG_NAME).append("\"); ");
                    break;
            }
        }
        s.append("management.commit(); ");

        return s.toString();
    }

    public JsonNode initSchema() {
        ArrayNode arrayNode = mapper.createArrayNode();
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

        arrayNode.add(nodeFactory.objectNode()
                .put("schema","VertexLabel")
                .put("name","person")
        );
        arrayNode.add(nodeFactory.objectNode()
                .put("schema","VertexLabel")
                .put("name","organization")
        );
        arrayNode.add(nodeFactory.objectNode()
                .put("schema","VertexLabel")
                .put("name","writing")
        );
        arrayNode.add(nodeFactory.objectNode()
                .put("schema","VertexLabel")
                .put("name","subject")
        );

        arrayNode.add(nodeFactory.objectNode()
                .put("schema","EdgeLabel")
                .put("name","create")
                .put("multiplicity", "MULTI")
        );
        arrayNode.add(nodeFactory.objectNode()
                .put("schema","EdgeLabel")
                .put("name","parent")
                .put("multiplicity", "MULTI")
        );
        arrayNode.add(nodeFactory.objectNode()
                .put("schema","EdgeLabel")
                .put("name","assessment")
                .put("multiplicity", "MULTI")
        );
        arrayNode.add(nodeFactory.objectNode()
                .put("schema","EdgeLabel")
                .put("name","belong")
                .put("multiplicity", "MULTI")
        );

        arrayNode.add(nodeFactory.objectNode()
                .put("schema","PropertyKey")
                .put("name","name")
                .put("dataType", "String")
        );
        arrayNode.add(nodeFactory.objectNode()
                .put("schema","PropertyKey")
                .put("name","start")
                .put("dataType", "Date")
        );
        arrayNode.add(nodeFactory.objectNode()
                .put("schema","PropertyKey")
                .put("name","end")
                .put("dataType", "Date")
        );
        arrayNode.add(nodeFactory.objectNode()
                .put("schema","PropertyKey")
                .put("name","role")
                .put("dataType", "String")
        );
        arrayNode.add(nodeFactory.objectNode()
                .put("schema","PropertyKey")
                .put("name","summary")
                .put("dataType", "String")
        );
        arrayNode.add(nodeFactory.objectNode()
                .put("schema","PropertyKey")
                .put("name","sortOf")
                .put("dataType", "String")
        );
        arrayNode.add(nodeFactory.objectNode()
                .put("schema","PropertyKey")
                .put("name","linkUrl")
                .put("dataType", "String")
        );
        arrayNode.add(nodeFactory.objectNode()
                .put("schema","PropertyKey")
                .put("name","rate")
                .put("dataType", "Float")
        );
        arrayNode.add(nodeFactory.objectNode()
                .put("schema","PropertyKey")
                .put("name","moralityIndex")
                .put("dataType", "Float")
        );
        arrayNode.add(nodeFactory.objectNode()
                .put("schema","PropertyKey")
                .put("name","performanceIndex")
                .put("dataType", "Float")
        );
        arrayNode.add(nodeFactory.objectNode()
                .put("schema","PropertyKey")
                .put("name","communicationIndex")
                .put("dataType", "Float")
        );
        arrayNode.add(nodeFactory.objectNode()
                .put("schema","PropertyKey")
                .put("name","ConsistencyIndex")
                .put("dataType", "Float")
        );
        arrayNode.add(nodeFactory.objectNode()
                .put("schema","PropertyKey")
                .put("name","ClassismIndex")
                .put("dataType", "Float")
        );
        arrayNode.add(nodeFactory.objectNode()
                .put("schema","PropertyKey")
                .put("name","JEIndex")
                .put("dataType", "Float")
        );

        ObjectNode idxSubject = nodeFactory.objectNode();
        ArrayNode keys = idxSubject.putArray("keys");
        keys.add("name");
        keys.add("sortOf");
        keys.add("start");
        arrayNode.add(idxSubject
                .put("schema","Index")
                .put("name","idxSubject")
                .put("indexFor", "Vertex")
        );

        return arrayNode;
    }
}
