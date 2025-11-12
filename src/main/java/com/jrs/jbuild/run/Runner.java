package com.jrs.jbuild.run;

import com.jrs.jbuild.JBuilder;
import com.jrs.jbuild.data.CompileContext;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Collectors;

public class Runner {
    public static final String ORANGE = "\u001B[38;5;208m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String LIGHT_GREEN = "\u001B[92m";
    public static final String LIGHT_BLUE = "\u001B[94m";

    public static void run(JBuilder builder, CompileContext ctx, String mainClass)
            throws IOException, InterruptedException {

        // Start with build/classes directory in the classpath
        String classpath = ctx.target.toString();

        // Add all JARs in libs/
//        if (Files.exists(ctx.libs())) {
//            String libs = Files.list(ctx.libs())
//                    .filter(p -> p.toString().endsWith(".jar"))
//                    .map(Path::toString)
//                    .collect(Collectors.joining(System.getProperty("path.separator")));
//            if (!libs.isEmpty()) {
//                classpath += System.getProperty("path.separator") + libs;
//            }
//        }

            String libs = ctx.classes.stream()
                    .filter(p -> p.toString().endsWith(".jar"))
                    .map(Path::toString)
                    .collect(Collectors.joining(System.getProperty("path.separator")));
            if (!libs.isEmpty()) {
                classpath += System.getProperty("path.separator") + libs;
            }

        // Run the compiled main class
        ProcessBuilder pb = new ProcessBuilder(
                "java", "-cp", classpath, mainClass);

        pb.directory(builder.ProjectRoot().toFile());
        pb.inheritIO(); // show stdout/stderr in current terminal
        Process process = pb.start();
        int code = process.waitFor();

        if (code == 0)
            System.out.println(LIGHT_BLUE+"[DEBUG] Program exited successfully"+RESET);
        else
            System.out.println(RED+"[ERROR] Program exited with code " + code+RESET);
    }
}
