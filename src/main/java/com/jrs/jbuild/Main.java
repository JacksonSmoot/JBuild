package com.jrs.jbuild;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static final String ORANGE = "\u001B[38;5;208m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String LIGHT_GREEN = "\u001B[92m";
    public static final String LIGHT_BLUE = "\u001B[94m";
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println(RED+"[ERROR] Usage: java -jar jbuild.jar <path-to-build-class>"+RESET);
            return;
        }

        // Step 1 — Parse build file path
        Path buildFile = Paths.get(args[0]).toAbsolutePath().normalize();

        if (!Files.exists(buildFile)) {
            System.err.println(RED+"[ERROR] Build file not found: " + buildFile+RESET);
            return;
        }

        // Step 2 — Determine directories
        Path root = buildFile.getParent();
        Path buildDir = root.resolve("build");
        Path classesDir = buildDir.resolve("classes");
        Files.createDirectories(classesDir);

        // Step 3 — Compile the build file
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.err.println(RED+"[ERROR] No system compiler found. Are you running on a JRE?"+RESET);
            return;
        }

        List<String> argsList = new ArrayList<>();
        argsList.add("-d");
        argsList.add(classesDir.toString());
        argsList.add(buildFile.toString());

        int result = compiler.run(null, null, null, argsList.toArray(String[]::new));
        if (result != 0) {
            System.err.println(RED+"[ERROR] Failed to compile build file."+RESET);
            return;
        }

        // Step 4 — Derive class name from file path
        String fileName = buildFile.getFileName().toString();
        String simpleClass = fileName.substring(0, fileName.lastIndexOf('.'));
        // System.out.println("fileName");

        // Assume no package for now (can expand later)
        System.out.println(GREEN+"[INFO] Compiled: " + simpleClass+RESET);

        // Step 5 — Load compiled class
        URLClassLoader loader = new URLClassLoader(new URL[]{classesDir.toUri().toURL()});
        System.out.println(loader.getURLs()[0].toString());
        System.out.println(simpleClass);
        Class<?> buildClass = Class.forName(simpleClass, true, loader);

        // Step 6 — Verify it extends JBuilder
        if (!JBuilder.class.isAssignableFrom(buildClass)) {
            System.err.println(RED+"[ERROR] " + simpleClass + " does not extend JBuilder."+RESET);
            return;
        }
        List<String> inputList = Arrays.asList(args);
        Object builder = buildClass.getDeclaredConstructor().newInstance();
        if(inputList.contains("--build")){
            Method buildMethod = buildClass.getMethod("build");
            buildMethod.invoke(builder);
            Method actualBuildMethod = builder.getClass().getMethod("finalBuild");
            actualBuildMethod.invoke(builder);
        }
        if(inputList.contains("--compile")){
            Method buildMethod = buildClass.getMethod("compile");
            buildMethod.invoke(builder);
            Method actualBuildMethod = builder.getClass().getMethod("finalCompile");
            actualBuildMethod.invoke(builder);
        }
        if(inputList.contains("--run")){
            Method runMethod = builder.getClass().getMethod("run");
            runMethod.invoke(builder);
            Method actualRunMethod = builder.getClass().getMethod("finalRun");
            actualRunMethod.invoke(builder);
        }
        // Step 7 — Create instance and run
        System.out.println(GREEN+"[INFO] Build completed."+RESET);
        deleteDirectory(buildDir);
    }

    public static void deleteDirectory(Path dir) throws IOException {
        if (!Files.exists(dir)) return;

        // Walk the directory tree from deepest to shallowest
        Files.walk(dir)
                .sorted(Comparator.reverseOrder()) // delete children before parents
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        System.err.println("Failed to delete " + path + ": " + e.getMessage());
                    }
                });

        System.out.println("Deleted directory: " + dir);
    }
}
