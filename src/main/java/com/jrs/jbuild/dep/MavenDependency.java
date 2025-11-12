package com.jrs.jbuild.dep;

import com.jrs.jbuild.dep.abst.AbstractDependency;

import java.nio.file.Path;

public class MavenDependency extends AbstractDependency {
    private String groupId;
    private String artifactId;
    private String version;
    public MavenDependency(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.path = Path.of(".");
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
