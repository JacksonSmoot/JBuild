package com.jrs.jbuild.dep;

import com.jrs.jbuild.dep.abst.AbstractDependency;

import java.nio.file.Path;

public class LocalDependency extends AbstractDependency {
    public LocalDependency(Path of) {
        super(of);
    }
}
