package de.palsoftware.tools.maven.git.autover;

import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.junit.Before;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Base class for maven testing.
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public abstract class MavenBaseTest extends BaseTest {

    protected MavenSession mavenSession;
    protected AutoverSession autoverSession;
    protected String localRepositoryPath;
    protected File multiModuleProjectDirectory;
    protected Model model1;
    protected Model model2;
    protected Model model3;
    protected MavenProject project1;
    protected MavenProject project2;
    protected MavenProject project3;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        autoverSession = new AutoverSession();
        final DefaultPlexusContainer plexusContainer = new DefaultPlexusContainer();
        final DefaultMavenExecutionRequest mavenExecutionRequest = new DefaultMavenExecutionRequest();
        multiModuleProjectDirectory = new File("tst");
        mavenExecutionRequest.setMultiModuleProjectDirectory(multiModuleProjectDirectory);
        final File localRepositoryFile = new File("tst", "aaa.xxx");
        localRepositoryPath = localRepositoryFile.getAbsolutePath();
        final MavenArtifactRepository localRepository = new MavenArtifactRepository();
        localRepository.setUrl(localRepositoryFile.toURI().toURL().toString());
        mavenExecutionRequest.setLocalRepository(localRepository);
        mavenExecutionRequest.setSystemProperties(new Properties());
        final DefaultMavenExecutionResult mavenExecutionResult = new DefaultMavenExecutionResult();
        final ArrayList<MavenProject> projects = new ArrayList<>();
        model1 = new Model();
        model1.setGroupId("de.group1");
        model1.setArtifactId("artifact1");
        model1.setVersion("0.0.0-SNAPSHOT");
        project1 = new MavenProject(model1);
        projects.add(project1);
        model2 = new Model();
        model2.setGroupId("de.group1");
        model2.setArtifactId("artifact2");
        model2.setVersion("0.0.0-SNAPSHOT");
        project2 = new MavenProject(model2);
        projects.add(project2);
        model3 = new Model();
        model3.setGroupId("de.group2");
        model3.setArtifactId("artifact1");
        model3.setVersion("0.0.0-SNAPSHOT");
        project3 = new MavenProject(model3);
        projects.add(project3);
        mavenSession = new MavenSession(plexusContainer, mavenExecutionRequest, mavenExecutionResult, projects);
        mavenSession.setAllProjects(projects);
    }
}
