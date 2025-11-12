package com.jrs.jbuild.dep.abst;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AbstractDependency {
    protected Path path;
    public AbstractDependency() {
        path = Paths.get(".");
    }

    public AbstractDependency(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    // public abstract void resolve();
}
