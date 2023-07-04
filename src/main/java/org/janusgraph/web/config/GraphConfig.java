package org.janusgraph.web.config;

import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

@Configuration
@PropertySource("classpath:conf/remote-objects.yaml")
public class GraphConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphConfig.class);
    public static final String CONF_GRAPH_PROPERTIES = "conf/remote-graph.properties";

    private final Environment environment;

    public GraphConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public GraphTraversalSource graphTraversalSource() throws Exception {
        return traversal().withRemote(CONF_GRAPH_PROPERTIES);
    }

    @Bean
    public JanusGraph janusGraph() {
        return JanusGraphFactory.open("conf/remote-graph.properties");
    }

    @Bean
    public JanusGraphManagement janusGraphManagement(JanusGraph janusGraph) {
        return janusGraph.openManagement();
    }

    @Bean
    public Cluster cluster() {
        String hosts = environment.getProperty("hosts", String.class);
        String[] contactPoints = null;
        if(hosts != null & hosts.startsWith("[") && hosts.endsWith("]")) {
            contactPoints = hosts.substring(1, hosts.length() - 1).split(",");
        }
        Integer port = environment.getProperty("port", Integer.class);

        if(contactPoints != null && contactPoints.length > 0 && port != null && port > 8000) {
            Cluster.Builder builder = Cluster.build();
            for (String address : contactPoints) {
                builder.addContactPoint(address);
            }
            builder.port(port);
            return builder.create();
        } else {
            return null;
        }
    }

    @Bean
    public Client client(Cluster cluster) {
        return cluster.connect();
    }
}
