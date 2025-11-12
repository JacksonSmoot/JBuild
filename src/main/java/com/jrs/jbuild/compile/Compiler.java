package com.jrs.jbuild.compile;

import com.jrs.jbuild.data.BuildContext;
import com.jrs.jbuild.data.CompileContext;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Compiler {
    public static final String ORANGE = "\u001B[38;5;208m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String LIGHT_GREEN = "\u001B[92m";
    public static final String LIGHT_BLUE = "\u001B[94m";
    private final CompileContext compileContext;
    public Compiler(BuildContext buildContext, CompileContext compileContext) {
        this.compileContext = compileContext;
    }

    public boolean compile() throws java.io.IOException{
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.err.println(RED+"[ERROR] No compiler found (use a JDK, not a JRE)."+RESET);
            return false;
        }

        List<String> args = new ArrayList<>();
        if(compileContext.javaVersion>0) {
            args.add("--release");
            args.add(String.valueOf(compileContext.javaVersion));
        }

        args.add("-d");
        args.add(compileContext.target.toAbsolutePath().toString());
        if (!compileContext.libs.isEmpty()) {
            args.add("-classpath");
            args.add(compileContext.libs);
        }

        // Collect source files
        List<String> sources = Files.walk(compileContext.src)
                .filter(p -> p.toString().endsWith(".java"))
                .map(Path::toString)
                .toList();
        // System.out.println(sources.toString());
        if (sources.isEmpty()) {
            System.err.println(RED+"[ERROR] No sources found in " + compileContext.src+RESET);
            return false;
        }

        args.addAll(sources);
        // System.out.println(args.toString());
        System.out.println(LIGHT_BLUE+"[INFO] Compiling " + sources.size() + " sources for Java " + compileContext.javaVersion + "..."+RESET);
        int result = compiler.run(null, null, null, args.toArray(String[]::new));
        return result == 0;
    }
}
