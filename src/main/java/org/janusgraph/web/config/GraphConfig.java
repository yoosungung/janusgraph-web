package org.janusgraph.web.config;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.util.system.ConfigurationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Closeable;
import java.io.IOException;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

@Configuration
public class GraphConfig implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphConfig.class);
    public static final String CONF_GRAPH_PROPERTIES = "conf/remote-graph.properties";

    private final org.apache.commons.configuration2.Configuration configuration;
    private Cluster cluster;
    private Client client;
    private GraphTraversalSource g;

    public GraphConfig() throws ConfigurationException {
        this.configuration = ConfigurationUtil.loadPropertiesConfig(CONF_GRAPH_PROPERTIES);
    }

    @Bean
    public GraphTraversalSource graphTraversalSource()  {
        g = traversal().withRemote(configuration);
        return g;
    }

    @Bean
    public Client client() throws ConfigurationException {
        try {
            cluster = Cluster.open(configuration.getString("gremlin.remote.driver.clusterFile"));
            client = cluster.connect();
        } catch (Exception e) {
            throw new ConfigurationException(e);
        }
        return client;
    }

    @Override
    public void close() throws IOException {
        try {
            if(g != null) g.close();
            if(cluster != null) cluster.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            g = null;
            client = null;
            cluster = null;
        }
    }
}
