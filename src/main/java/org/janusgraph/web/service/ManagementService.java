package org.janusgraph.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.springframework.stereotype.Service;

@Service
public class ManagementService {
    private final ObjectMapper objectMapper;
    private final JanusGraphManagement mgmt;

    public ManagementService(JanusGraphManagement mgmt) throws Exception {
        this.mgmt = mgmt;
        this.objectMapper = new ObjectMapper();
    }
}
