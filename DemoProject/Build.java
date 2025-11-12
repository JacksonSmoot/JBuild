import com.jrs.jbuild.JBuilder;

public class Build extends JBuilder {
    @Override
    public void build() {
        SetProjectRoot(ProjectRoot("DemoProject"));
        AddMavenDependency("org.json", "json", "20250517");
        SetSourceDirectory(ProjectRoot("src", "main", "java"));
        SetMainClass("Test");
    }

    @Override
    public void compile() {
        SetJavaRelease(25);
        SetTargetScope("compile");
        SetTargetDirectory(ProjectRoot("target"));
    }

    @Override
    public void run() {

    }
}
