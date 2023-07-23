package org.janusgraph.web.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.tinkerpop.gremlin.process.traversal.Bindings;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TraversalService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraversalService.class);

    private static final String LABEL = "label";
    private static final String OUT_V = "outV";
    private static final String IN_V = "inV";
    private static final List<String> KEY_WORDS = List.of(LABEL,OUT_V,IN_V);

    private final GraphTraversalSource g;

    public TraversalService(GraphTraversalSource g) {
        this.g = g;
    }

    public String getVertexSchema(String label) {
        final StringBuilder s = new StringBuilder();
        s.append("{");
        s.append("\"label\":\"").append(label).append("\",");
        s.append("\"name\":\"String\",");
        if("person".equals(label)) {
            s.append("\"avatarUrl\":\"String\"");
        }
        s.append("\"start\":\"Date\",");
        if("person".equals(label) || "organization".equals(label)) {
            s.append("\"end\":\"Date\",");
        }
        s.append("\"sortOf\":\"String\",");
        if("person".equals(label) || "organization".equals(label)) {
            s.append("\"role\":\"String\",");
        }
        s.append("\"summary\":\"String\",");
        s.append("\"linkUrl\":\"String\"");
        if("writing".equals(label) || "subject".equals(label)) {
            s.append(",\"moralityIndex\":\"Float\",");
            s.append("\"performanceIndex\":\"Float\",");
            s.append("\"communicationIndex\":\"Float\",");
            s.append("\"ConsistencyIndex\":\"Float\",");
            s.append("\"ClassismIndex\":\"Float\",");
            s.append("\"JEIndex\":\"Float\"");
        }
        s.append("}");
        return s.toString();
    }

    public String getEdgeSchema(String label) {
        final StringBuilder s = new StringBuilder();
        s.append("{");
        s.append("\"label\":\"").append(label).append("\",");
        s.append("\"outV\":\"Vertex.id\",");
        s.append("\"inV\":\"Vertex.id\",");
        s.append("\"start\":\"Date\",");
        switch (label) {
            case "create":
            case "parent":
                break;
            case "assessment":
                s.append("\"rate\":\"Float\",");
                break;
            case "belong":
                s.append("\"end\":\"Date\",");
                s.append("\"role\":\"String\",");
                break;
        }
        s.append("\"linkUrl\":\"String\"");
        s.append("}");
        return s.toString();
    }

    public String getAllSchema() {
        final StringBuilder s = new StringBuilder();
        s.append("{");
        s.append("\"vertex\": [");
        s.append(getVertexSchema("person")).append(",");
        s.append(getVertexSchema("organization")).append(",");
        s.append(getVertexSchema("writing")).append(",");
        s.append(getVertexSchema("subject"));
        s.append("],");
        s.append("\"edge\": [");
        s.append(getEdgeSchema("create")).append(",");
        s.append(getEdgeSchema("parent")).append(",");
        s.append(getEdgeSchema("assessment")).append(",");
        s.append(getEdgeSchema("belong"));
        s.append("]");
        s.append("}");
        return s.toString();
    }

    private static void setBody(JsonNode body, Bindings bindings, GraphTraversal<?, ?> result) {
        for (Iterator<String> it = body.fieldNames(); it.hasNext(); ) {
            String fieldName = it.next();
            if(! KEY_WORDS.contains(fieldName)) {
                Object fieldValue = null;
                switch (body.get(fieldName).getNodeType()) {
                    case STRING:
                        fieldValue = body.get(fieldName).asText();
                        break;
                    case NUMBER:
                        fieldValue = body.get(fieldName).asDouble();
                        break;
                    case BOOLEAN:
                        fieldValue = body.get(fieldName).asBoolean();
                        break;
                }
                result.property(fieldName, bindings.of(fieldName, fieldValue));
            }
        }
    }

    public Vertex makeVertex(JsonNode body) {
        LOGGER.info("creating elements: {}", body);
        final Bindings bindings = Bindings.instance();

        GraphTraversal<Vertex, Vertex> result = g.addV(bindings.of(LABEL, body.get(LABEL).asText()));
        setBody(body, bindings, result);

        return result.next();
    }

    public Edge makeEdge(JsonNode body) {
        LOGGER.info("creating edge: {}", body);
        final Bindings bindings = Bindings.instance();

        GraphTraversal<Vertex, Edge> result = g.V(bindings.of(OUT_V, findVertexById(body.get(OUT_V).asText()))).as("a")
                .V(bindings.of(IN_V, findVertexById(body.get(IN_V).asText())))
                .addE(bindings.of(LABEL, body.get(LABEL).asText()));
        setBody(body, bindings, result);

        return result.from("a").next();
    }
    public Vertex findVertexById(String id) {
        return g.V(Long.parseLong(id)).next();
    }

    public Map<Object, Object> valuesVertexById(String id) {
        return valuesVertexById(Long.parseLong(id));
    }
    public Map<Object, Object> valuesVertexById(Long id) {
        Map<Object, Object> map = new HashMap<>();
        Vertex v = g.V(id).next();
        map.put("id", v.id());
        map.put("label", v.label());
        map.putAll(g.V(id).valueMap().next());
        return map;
    }
    public List<Map<Object, Object>> valyeVertexByIdlist(List<Long> idList) {
        List<Map<Object, Object>> result = new ArrayList<>();
        for(Long id : idList) {
            result.add(valuesVertexById(id));
        }
        return result;
    }

    public List<Vertex> findVertexByParams(String label, Map<String, String> params) {
        GraphTraversal<Vertex, Vertex> gt = g.V().hasLabel(label);
        for(String key: params.keySet()) {
            gt = gt.has(key, params.get(key));
        }
        return gt.next(Integer.MAX_VALUE);
    }

    public Map<Object, Object> valuesEdgeById(String id) {
        Map<Object, Object> map = new HashMap<>();
        Edge e = g.E(id).next();
        map.put("id", e.id().toString());
        map.put("label", e.label());
        map.put("outV", e.outVertex().id());
        map.put("inV", e.inVertex().id());
        map.putAll(g.E(id).valueMap().next());
        return map;
    }

    public List<Edge> findOutEdgeByVertexId(String id) {
        return g.V(Long.parseLong(id)).outE().next(Integer.MAX_VALUE);
    }

    public List<Edge> findInEdgeByVertexId(String id) {
        return g.V(Long.parseLong(id)).inE().next(Integer.MAX_VALUE);
    }

}
