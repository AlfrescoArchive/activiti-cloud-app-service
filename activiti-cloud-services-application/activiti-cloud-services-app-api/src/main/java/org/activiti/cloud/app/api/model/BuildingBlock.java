package org.activiti.cloud.app.api.model;

public interface BuildingBlock {

    public enum TYPE {
        MAVEN,
        DOCKER
    }

    String getId();

    String getGroup();

    String getName();

    String getVersion();

    TYPE getType();
}
