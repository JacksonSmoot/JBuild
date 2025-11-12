package com.jrs.jbuild;

import com.jrs.jbuild.compile.Compiler;
import com.jrs.jbuild.data.BuildContext;
import com.jrs.jbuild.data.CompileContext;
import com.jrs.jbuild.dep.LocalDependency;
import com.jrs.jbuild.dep.MavenDependency;
import com.jrs.jbuild.dep.SystemMavenDownloader;
import com.jrs.jbuild.dep.abst.AbstractDependency;
import com.jrs.jbuild.run.Runner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public abstract class JBuilder {
    public BuildContext buildContext;
    public CompileContext compileContext;
    public JBuilder(){
        buildContext = new BuildContext();
        compileContext = new CompileContext();
    }

    public abstract void build();

    public abstract void compile();

    public abstract void run();

    // compile instruction managment
    public final void SetJavaRelease(int version) {
        compileContext.javaVersion = version;
    }

    public final void SetTargetScope(String scope){
        if(scope == null || (
                !scope.equals("compile") && !scope.equals("runtime")  && !scope.equals("test")
                && !scope.equals("jar")  && !scope.equals("class")
                )){
            throw new IllegalArgumentException("Invalid scope.");
        }
    }

    // path managment
    public final void SetMainClass(String mainClass){
        buildContext.mainClass = mainClass;
    }

    public final void SetProjectRoot(Path root){
        buildContext.root = root;
    }

    public final void SetProjectRoot(String first, String... more){
        buildContext.root = Paths.get(first, more);
    }

    // dependency managment
    public final void AddLocalJar(String jarPath){
        buildContext.dependencies.add(new LocalDependency(Path.of(jarPath)));
    }

    public final void AddLocalJar(Path jarPath){
        buildContext.dependencies.add(new LocalDependency(jarPath));
    }

    public final void AddMavenDependency(String groupId, String artifactId, String version){
        buildContext.dependencies.add(new MavenDependency(groupId, artifactId, version));
    }

    public final void AddMavenDependency(MavenDependency dependency){
        buildContext.dependencies.add(dependency);
    }

    public final void SetSourceDirectory(Path sourceDirectory){
        buildContext.source = sourceDirectory;
    }

    public final void SetSourceDirectory(String first, String... more){
        buildContext.source = Paths.get(first, more);
    }

    // Paths
    public final Path UserHome(){
        return BuildContext.HOME;
    }

    public final Path ProjectRoot(){
        return buildContext.root;
    }

    public final Path ProjectRoot(String first, String... more){
        return buildContext.root.resolve(first, more);
    }

    public final void SetTargetDirectory(Path directory){
        compileContext.target = directory;
    }

    public final void SetTargetDirectory(String first, String... more){
        compileContext.target = Paths.get(first, more);
    }

    // builds
    public final void finalBuild(){
        System.out.println(buildContext.mainClass+": "+buildContext.root+": "+buildContext.dependencies.toString());
        SystemMavenDownloader mvd = new SystemMavenDownloader();
        for(AbstractDependency dependency : buildContext.dependencies) {
            if (dependency instanceof LocalDependency local) {
                compileContext.classes.add(local.getPath());
            } else if (dependency instanceof MavenDependency) {
                try {
                    List<File> fileList = mvd.resolve(dependency);
                    for (File file : fileList) {
                        compileContext.classes.add(file.toPath());
                    }
                } catch (Exception e) {
                    System.err.println("Failed to resolve maven dependency's: " + e.getMessage());
                }
            }
        }
//        for(Path path : compileContext.classes){
//            System.out.println(path.toString());
//        }
    }

    public final void finalCompile(){
        compileContext.src = buildContext.source;
        String classpath = compileContext.classes.stream()
                .map(Path::toString)
                .collect(Collectors.joining(File.pathSeparator));
        compileContext.libs = classpath;
        Compiler c = new Compiler(buildContext, compileContext);
        try {
            c.compile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(compileContext.javaVersion);
    }

    public final void finalRun(){
        try{
            Runner.run(this, compileContext, buildContext.mainClass);
        }catch(Exception e){
            throw new RuntimeException(e);
        }

    }
}
