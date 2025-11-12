package com.jrs.jbuild.data;

import com.jrs.jbuild.dep.abst.AbstractDependency;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BuildContext {
    public static final Path HOME = Paths.get(System.getProperty("user.home"));
    public List<AbstractDependency> dependencies = new ArrayList<>();
    public Path root = Paths.get(System.getProperty("user.dir"));
    public String mainClass = "";
    public Path source = Paths.get(System.getProperty("user.dir"));
    public BuildContext() {}
}
