package com.jrs.jbuild.dep.abst;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public abstract class DependencyResolver {
    public DependencyResolver() {}

    public abstract List<File> resolve(AbstractDependency dep) throws Exception;
}
