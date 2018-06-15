package de.palsoftware.tools.maven.git.autover;

import de.palsoftware.tools.maven.git.autover.conf.AutoverConfig;
import org.apache.maven.MavenExecutionException;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Test class for {@link MavenLifecycleListener}
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class MavenLifecycleListenerTest extends MavenBaseTest {

    protected AutoverConfigDecorator autoverConfigDecorator;
    private MavenLifecycleListener mavenLifecycleListener;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        mavenLifecycleListener = new MavenLifecycleListener();
        mavenLifecycleListener.setAutoverSession(autoverSession);
        mavenLifecycleListener.setLogger(new ConsoleLogger());
        final AutoverConfig defaultConfiguration = ConfigHelper.getDefaultConfiguration();
        autoverConfigDecorator = new AutoverConfigDecorator(defaultConfiguration);
    }

    @Test
    public void afterSessionStart() throws MavenExecutionException {
        mavenLifecycleListener.afterSessionStart(mavenSession);
        assertTrue("MavenLifecycleListener -> afterSessionStart problem!", multiModuleProjectDirectory.equals(autoverSession.getMavenMultiModuleProjectDir()));
        assertTrue("MavenLifecycleListener -> afterSessionStart problem!", autoverConfigDecorator.equals(autoverSession.getConfig()));
    }

    @Test
    public void afterProjectsRead() throws IOException {
        final Map<String, File> newPomFiles = autoverSession.getNewPomFiles();
        final File file1 = createTmpFile();
        newPomFiles.put(model1.getId(), file1);
        final File file2 = createTmpFile();
        newPomFiles.put(model2.getId(), file2);
        final File file3 = createTmpFile();
        newPomFiles.put(model3.getId(), file3);
        mavenLifecycleListener.afterProjectsRead(mavenSession);

        Assert.assertTrue("MavenLifecycleListener -> afterProjectsRead problem!", file1.equals(project1.getFile()));
        Assert.assertTrue("MavenLifecycleListener -> afterProjectsRead problem!", file2.equals(project2.getFile()));
        Assert.assertTrue("MavenLifecycleListener -> afterProjectsRead problem!", file3.equals(project3.getFile()));
    }
}