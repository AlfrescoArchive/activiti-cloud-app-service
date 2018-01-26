package org.activiti.cloud.app.api.model;

public interface ActivitiCloudAppDeployer {

    String deploy(BuildingBlock bb);

    String getStatus(String provisionId);
}
