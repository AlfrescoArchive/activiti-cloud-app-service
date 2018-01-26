package org.activiti.cloud.app.api.model;

import java.util.Date;

import org.activiti.cloud.organization.core.model.Project;

public class ProjectRelease {

    private Project project;
    private String version;
    private Date releaseDate;
    private String releaseName;

    public ProjectRelease(Project project,
                          String version,
                          Date releaseDate,
                          String releaseName) {
        this.project = project;
        this.version = version;
        this.releaseDate = releaseDate;
        this.releaseName = releaseName;
    }

    public Project getProject() {
        return project;
    }

    public String getVersion() {
        return version;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public String getReleaseName() {
        return releaseName;
    }
}
