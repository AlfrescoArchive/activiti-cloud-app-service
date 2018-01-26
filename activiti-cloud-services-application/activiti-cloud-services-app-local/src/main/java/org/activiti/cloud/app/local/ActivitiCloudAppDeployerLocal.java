package org.activiti.cloud.app.local;

import java.util.HashMap;
import java.util.Map;

import org.activiti.cloud.app.api.model.ActivitiCloudAppDeployer;
import org.activiti.cloud.app.api.model.BuildingBlock;
import org.springframework.cloud.deployer.resource.docker.DockerResource;
import org.springframework.cloud.deployer.resource.maven.MavenResource;
import org.springframework.cloud.deployer.spi.core.AppDefinition;
import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;
import org.springframework.cloud.deployer.spi.local.LocalDeployerProperties;
import org.springframework.cloud.deployer.spi.local.LocalTaskLauncher;
import org.springframework.core.io.AbstractResource;

public class ActivitiCloudAppDeployerLocal implements ActivitiCloudAppDeployer {

    private LocalTaskLauncher launcher = new LocalTaskLauncher(new LocalDeployerProperties());

    @Override
    public String deploy(BuildingBlock bb) {
        return launcher.launch(createAppDeploymentRequest(bb.getGroup(),
                                                          bb.getName(),
                                                          bb.getVersion(),
                                                          bb.getType()));
    }

    @Override
    public String getStatus(String provisionId) {
        return launcher.status(provisionId).toString();
    }

    private AppDeploymentRequest createAppDeploymentRequest(String group,
                                                            String app,
                                                            String version,
                                                            BuildingBlock.TYPE type) {
        AbstractResource resource;
        if (type.equals(BuildingBlock.TYPE.MAVEN)) {
            resource = new MavenResource.Builder()
                    .artifactId(app)
                    .groupId(group)
                    .version(version)
                    .build();
        } else if (type.equals(BuildingBlock.TYPE.DOCKER)) {
            resource = new DockerResource(group + "/" + app + ":" + version);
        } else {
            throw new IllegalStateException("Application Deployment Request must be provide a supported type");
        }

        Map<String, String> properties = new HashMap<>();
        properties.put("spring.rabbitmq.host",
                       "localhost");
        properties.put("eureka.client.enabled",
                       "false");
//        properties.put("server.port",
//                       "0");
        AppDefinition definition = new AppDefinition(app,
                                                     properties);

        Map<String, String> deploymentProperties = new HashMap<>();
        deploymentProperties.put(LocalDeployerProperties.INHERIT_LOGGING,
                                 "true");

        AppDeploymentRequest request = new AppDeploymentRequest(definition,
                                                                resource,
                                                                deploymentProperties);

        return request;
    }
}
