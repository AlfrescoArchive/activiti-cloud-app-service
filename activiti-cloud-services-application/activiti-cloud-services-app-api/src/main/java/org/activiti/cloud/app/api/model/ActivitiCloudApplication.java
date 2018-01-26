package org.activiti.cloud.app.api.model;

import java.util.List;

public class ActivitiCloudApplication {

    private ProjectRelease projectRelease;
    private List<RuntimeBundleDesc> runtimeBundles;
    private List<CloudConnectorDesc> cloudConnectors;
    private List<ServiceDesc> services;

    public ActivitiCloudApplication() {
    }

    public ActivitiCloudApplication(ProjectRelease projectRelease,
                                    List<RuntimeBundleDesc> runtimeBundles,
                                    List<CloudConnectorDesc> cloudConnectors,
                                    List<ServiceDesc> services) {
        this.projectRelease = projectRelease;
        this.runtimeBundles = runtimeBundles;
        this.cloudConnectors = cloudConnectors;
        this.services = services;
    }

    public String getId() {
        return getName() + ":" + getVersion();
    }

    public String getName() {
        return projectRelease.getReleaseName();
    }

    public String getVersion() {
        return projectRelease.getVersion();
    }

    public ProjectRelease getProjectRelease() {
        return projectRelease;
    }

    public List<RuntimeBundleDesc> getRuntimeBundles() {
        return runtimeBundles;
    }

    public List<CloudConnectorDesc> getCloudConnectors() {
        return cloudConnectors;
    }

    public List<ServiceDesc> getServices() {
        return services;
    }
}
