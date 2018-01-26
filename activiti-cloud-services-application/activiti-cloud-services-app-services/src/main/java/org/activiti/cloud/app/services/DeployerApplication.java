package org.activiti.cloud.app.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.activiti.cloud.app.api.model.ActivitiCloudAppDeployer;
import org.activiti.cloud.app.api.model.ActivitiCloudApplication;
import org.activiti.cloud.app.api.model.BuildingBlock;
import org.activiti.cloud.app.api.model.CloudConnectorDesc;
import org.activiti.cloud.app.api.model.ProjectRelease;
import org.activiti.cloud.app.api.model.QueryServiceDesc;
import org.activiti.cloud.app.api.model.RuntimeBundleDesc;
import org.activiti.cloud.app.api.model.ServiceDesc;
import org.activiti.cloud.app.local.ActivitiCloudAppDeployerLocal;
import org.activiti.cloud.organization.core.model.Model;
import org.activiti.cloud.organization.core.model.Project;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class DeployerApplication implements CommandLineRunner {

    public static void main(String[] args) throws InterruptedException {

        SpringApplication.run(DeployerApplication.class,
                              args);
    }

    private ActivitiCloudAppDeployer appDeployer = new ActivitiCloudAppDeployerLocal();
    private Map<String, Map<String, String>> provisionedApps = new ConcurrentHashMap<>();
    private Map<String, Map<String, String>> provisionedAppsStatus = new ConcurrentHashMap<>();

    public DeployerApplication() {
    }

    @Override
    public void run(String... args) throws Exception {

        ProjectRelease projectRelease = new ProjectRelease(new Project(),
                                                           "1.0.Final",
                                                           new Date(),
                                                           "hero-release");

        List<Model> models =
                projectRelease.getProject().getModels();

        // At this point we need to make a decision on how many runtime bundles
        // do we want for our Models and AutoGenerate them with yeoman

        RuntimeBundleDesc runtimeBundleDesc = new RuntimeBundleDesc("org.activiti.cloud.examples",
                                                                    "english-campaign-rb",
                                                                    "1.0.0-SNAPSHOT",
                                                                    BuildingBlock.TYPE.MAVEN);
        List<RuntimeBundleDesc> runtimeBundles = new ArrayList<>();
        runtimeBundles.add(runtimeBundleDesc);

        CloudConnectorDesc processingConnector = new CloudConnectorDesc("org.activiti.cloud.examples",
                                                                        "activiti-cloud-connectors-processing",
                                                                        "1.0.0-SNAPSHOT",
                                                                        BuildingBlock.TYPE.MAVEN);

        CloudConnectorDesc rankingConnector = new CloudConnectorDesc("org.activiti.cloud.examples",
                                                                     "activiti-cloud-connectors-ranking",
                                                                     "1.0.0-SNAPSHOT",
                                                                     BuildingBlock.TYPE.MAVEN);

        CloudConnectorDesc rewardsConnector = new CloudConnectorDesc("org.activiti.cloud.examples",
                                                                     "activiti-cloud-connectors-reward",
                                                                     "1.0.0-SNAPSHOT",
                                                                     BuildingBlock.TYPE.MAVEN);

        CloudConnectorDesc dummyTwitterConnector = new CloudConnectorDesc("org.activiti.cloud.examples",
                                                                          "activiti-cloud-connectors-dummytwitter",
                                                                          "1.0.0-SNAPSHOT",
                                                                          BuildingBlock.TYPE.MAVEN);

        List<CloudConnectorDesc> connectors = new ArrayList<>();
        connectors.add(processingConnector);
        connectors.add(rankingConnector);
        connectors.add(dummyTwitterConnector);

        List<ServiceDesc> services = new ArrayList<>();

        QueryServiceDesc queryServiceDesc = new QueryServiceDesc("org.activiti.cloud",
                                                                 "activiti-cloud-audit",
                                                                 "7-201801-EA-SNAPSHOT",
                                                                 BuildingBlock.TYPE.MAVEN);

        services.add(queryServiceDesc);

        ActivitiCloudApplication activitiCloudApplication =
                new ActivitiCloudApplication(projectRelease,
                                             runtimeBundles,
                                             connectors,
                                             services);

        provision(activitiCloudApplication);

        while (true) {
            Thread.sleep(5000);

            Map<String, String> appStatus = provisionedAppsStatus.get(activitiCloudApplication.getId());
            if (appStatus == null) {
                provisionedAppsStatus.put(activitiCloudApplication.getId(),
                                          new ConcurrentHashMap<>());
            }
            if (isActivitiCloudAppRunning(activitiCloudApplication)) {
                System.out.println("################################################################");
                System.out.println("-> Application: " + activitiCloudApplication.getId() + " is Running! ");
                System.out.println("################################################################");
            }
            System.out.println("####################### APPs Report #################################");
            for (String app : provisionedApps.keySet()) {
                System.out.println("\t\t APP: " + app + " Building Blocks Status");
                for (String bb : provisionedApps.get(app).keySet()) {
                    String status = appDeployer.getStatus(provisionedApps.get(app).get(bb));
                    provisionedAppsStatus.get(activitiCloudApplication.getId()).put(bb,
                                                                                    status);
                    System.out.println("\t\t\t\t\t Building Block:" + bb + " -> Status : " + status);
                }
            }

            System.out.println("####################### END APPs Report #################################");
        }
    }

    private boolean isActivitiCloudAppRunning(ActivitiCloudApplication activitiCloudApplication) {
        boolean running = true;
        if (activitiCloudApplication.getServices().isEmpty()
                && activitiCloudApplication.getRuntimeBundles().isEmpty()
                && activitiCloudApplication.getCloudConnectors().isEmpty()) {
            running = false;
        }
        for (ServiceDesc sd : activitiCloudApplication.getServices()) {
//            System.out.println(sd.getGroup() + ":" + sd.getName() + ":" + sd.getVersion() + " - > " + provisionedAppsStatus.get(sd.getGroup() + ":" + sd.getName() + ":" + sd.getVersion()));
            if (!"running".equals(provisionedAppsStatus.get(activitiCloudApplication.getId()).get(sd.getId()))) {
                running = false;
            }
        }
        for (CloudConnectorDesc cd : activitiCloudApplication.getCloudConnectors()) {
//            System.out.println(cd.getGroup() + ":" + cd.getName() + ":" + cd.getVersion() + " - > " + provisionedAppsStatus.get(cd.getGroup() + ":" + cd.getName() + ":" + cd.getVersion()));
            if (!"running".equals(provisionedAppsStatus.get(activitiCloudApplication.getId()).get(cd.getId()))) {
                running = false;
            }
        }
        for (RuntimeBundleDesc rbd : activitiCloudApplication.getRuntimeBundles()) {
//            System.out.println(rbd.getGroup() + ":" + rbd.getName() + ":" + rbd.getVersion() + " - > " + provisionedAppsStatus.get(rbd.getGroup() + ":" + rbd.getName() + ":" + rbd.getVersion()));
            if (!"running".equals(provisionedAppsStatus.get(activitiCloudApplication.getId()).get(rbd.getId()))) {
                running = false;
            }
        }
        return running;
    }

    public void provision(ActivitiCloudApplication app) {
        if (app == null) {
            throw new IllegalStateException("App to provision cannot be null");
        }
        if (app.getServices().isEmpty()
                && app.getRuntimeBundles().isEmpty()
                && app.getCloudConnectors().isEmpty()) {
            throw new IllegalStateException("The App doesn't have any building block to run");
        }
        Map<String, String> appEntry = provisionedApps.get(app.getId());
        if (appEntry == null) {
            provisionedApps.put(app.getId(),
                                new ConcurrentHashMap<>());
        }
        System.out.println(" - Starting Provisioning for App: " + app.getId());
        System.out.println("\t - Starting Services Provisioning ...");
        for (ServiceDesc sd : app.getServices()) {
            System.out.println("\t\t - Provisioning Service: " + sd.getName() + " - version: " + sd.getVersion());
            String provisionId = launchBuildingBlock(sd);
            provisionedApps.get(app.getId()).put(sd.getId(),
                                                 provisionId);
        }

        System.out.println("\t - Starting Cloud Connectors Provisioning ...");
        for (CloudConnectorDesc cd : app.getCloudConnectors()) {
            System.out.println("\t\t - Provisioning Cloud Connector: " + cd.getName() + " - version: " + cd.getVersion());
            String provisionId = launchBuildingBlock(cd);
            provisionedApps.get(app.getId()).put(cd.getId(),
                                                 provisionId);
        }

        System.out.println("\t - Starting Runtime Bundles Provisioning ...");
        for (RuntimeBundleDesc rbd : app.getRuntimeBundles()) {
            System.out.println("\t\t - Provisioning Runtime Bundle: " + rbd.getName() + " - version: " + rbd.getVersion());

            String provisionId = launchBuildingBlock(rbd);
            provisionedApps.get(app.getId()).put(rbd.getId(),
                                                 provisionId);
        }
    }

    public void unprovision(ActivitiCloudApplication app) {

    }

    private String launchBuildingBlock(BuildingBlock bb) {
        return appDeployer.deploy(bb);

    }


}
