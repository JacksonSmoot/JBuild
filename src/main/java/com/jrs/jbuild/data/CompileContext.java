package com.jrs.jbuild.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CompileContext {
    public int javaVersion = -1;
    public Path src;
    public List<Path> classes = new ArrayList<>();
    public String libs;
    public Path target;
}
