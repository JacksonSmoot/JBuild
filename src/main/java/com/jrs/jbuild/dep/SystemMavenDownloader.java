package com.jrs.jbuild.dep;

import com.jrs.jbuild.dep.abst.AbstractDependency;
import com.jrs.jbuild.dep.abst.DependencyResolver;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
//import org.eclipse.aether.*;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SystemMavenDownloader extends DependencyResolver {
    private final RepositorySystem system;
    private final RepositorySystemSession session;
    private final List<RemoteRepository> remotes;

    public SystemMavenDownloader() {
        this.system = RepositorySystemFactory.newRepositorySystem();
        this.session = newSession(system);
        this.remotes = List.of(
                new RemoteRepository.Builder("central", "default", "https://repo1.maven.org/maven2/").build()
        );
    }

    /**
     * Resolves an artifact and all transitive dependencies into ~/.m2/repository
     * Returns a list of resolved jar files.
     */
    @Override
    public List<File> resolve(AbstractDependency dep) throws Exception {
        String coords = "";
        if(dep instanceof MavenDependency mavenDependency) {
            coords = mavenDependency.getGroupId() + ":" + mavenDependency.getArtifactId()+":"+mavenDependency.getVersion();
        }
        else{
            System.err.println("Wrong variable type passed to maven resolver: "+dep.getClass());
            return Collections.emptyList();
        }
        System.out.println("🔍 Resolving " + coords);

        DefaultArtifact artifact = new DefaultArtifact(coords);
        Dependency rootDependency = new Dependency(artifact, "compile");

        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(rootDependency);
        collectRequest.setRepositories(remotes);

        DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, null);
        DependencyResult result = system.resolveDependencies(session, dependencyRequest);

        List<File> resolved = result.getArtifactResults().stream()
                .map(r -> r.getArtifact().getFile())
                .collect(Collectors.toList());

        System.out.println("✅ Installed " + resolved.size() + " dependencies for " + coords);
        return resolved;
    }

    // ----------------------------------------------------------------------

    @Deprecated
    private static RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        return locator.getService(RepositorySystem.class);
    }

    private static RepositorySystemSession newSession(RepositorySystem system) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        // Use the computer’s Maven repository (e.g. ~/.m2/repository)
        File localRepoDir = new File(System.getProperty("user.home"), ".m2/repository");
        LocalRepository localRepo = new LocalRepository(localRepoDir);
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

        // Checksum + update policies
        session.setChecksumPolicy(RepositoryPolicy.CHECKSUM_POLICY_WARN);
        session.setUpdatePolicy(RepositoryPolicy.UPDATE_POLICY_DAILY);

        System.out.println("📦 Using system Maven repository: " + localRepoDir);
        return session;
    }
}
