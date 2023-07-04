package org.janusgraph.web.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tinkerpop.gremlin.process.traversal.Bindings;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class TraversalService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraversalService.class);

    private static final String LABEL = "label";
    private static final String OUT_V = "outV";
    private static final String IN_V = "inV";

    private final GraphTraversalSource g;

    public TraversalService(GraphTraversalSource g) {
        this.g = g;
    }

    public Vertex createVertex(JsonNode body) {
        LOGGER.info("creating elements: {}", body);
        final Bindings bindings = Bindings.instance();

        GraphTraversal<Vertex, Vertex> result = g.addV(bindings.of(LABEL, body.get(LABEL).asText()));
        for (Iterator<String> it = body.fieldNames(); it.hasNext(); ) {
            String fieldName = it.next();
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
        return result.next();
    }

    public Edge createEdge(JsonNode body) {
        LOGGER.info("creating elements: {}", body);
        final Bindings bindings = Bindings.instance();

        GraphTraversal<Vertex, Edge> result = g.V(bindings.of(OUT_V, findVertex(body.get(OUT_V)))).as("a")
                .V(IN_V, findVertex(body.get(IN_V)))
                .addE(bindings.of(LABEL, body.get(LABEL).asText()));
        for (Iterator<String> it = body.fieldNames(); it.hasNext(); ) {
            String fieldName = it.next();
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
        return result.next();
    }

    private Vertex findVertex(JsonNode node) {
        return g.V(node.asText()).next();
    }
}
