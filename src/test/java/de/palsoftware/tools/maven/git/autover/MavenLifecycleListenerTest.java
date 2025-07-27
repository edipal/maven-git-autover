package de.palsoftware.tools.maven.git.autover;

import de.palsoftware.tools.maven.git.autover.conf.AutoverConfig;
import org.apache.maven.MavenExecutionException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Test class for {@link MavenLifecycleListener}
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class MavenLifecycleListenerTest extends MavenBaseTest {

    protected AutoverConfigDecorator autoverConfigDecorator;
    protected MavenLifecycleListener mavenLifecycleListener;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        mavenLifecycleListener = new MavenLifecycleListener();
        mavenLifecycleListener.setAutoverSession(autoverSession);
        final ConsoleLogger logger = new ConsoleLogger();
        logger.setThreshold(Logger.LEVEL_DEBUG);
        mavenLifecycleListener.setLogger(logger);
        final AutoverConfig defaultConfiguration = ConfigHelper.getDefaultConfiguration();
        autoverConfigDecorator = new AutoverConfigDecorator(defaultConfiguration);
    }

    @Test
    public void afterSessionStart() throws MavenExecutionException, IOException {

        //disabled = false
        //disabled pom change = false
        autoverSession.setMavenMultiModuleProjectDir(null);
        autoverSession.setConfig(null);

        mavenLifecycleListener.afterSessionStart(mavenSession);
        Assert.assertTrue("MavenLifecycleListener -> afterSessionStart problem!", multiModuleProjectDirectory.getCanonicalFile().equals(autoverSession.getMavenMultiModuleProjectDir()));
        Assert.assertTrue("MavenLifecycleListener -> afterSessionStart problem!", autoverConfigDecorator.equals(autoverSession.getConfig()));
        Assert.assertFalse("MavenLifecycleListener -> afterSessionStart problem!", autoverSession.isDisable());
        Assert.assertFalse("MavenLifecycleListener -> afterSessionStart problem!", autoverSession.isDisablePomChange());

        //disabled = true
        //disabled pom change = true
        autoverSession.setMavenMultiModuleProjectDir(null);
        autoverSession.setConfig(null);

        final Properties systemProperties = mavenSession.getSystemProperties();
        systemProperties.setProperty(MavenLifecycleListener.DISABLE_PROPERTY_KEY, Boolean.TRUE.toString());
        systemProperties.setProperty(MavenLifecycleListener.DISABLE_POM_CHANGE_PROPERTY_KEY, Boolean.TRUE.toString());
        mavenLifecycleListener.afterSessionStart(mavenSession);
        Assert.assertNull("MavenLifecycleListener -> afterSessionStart problem!", autoverSession.getMavenMultiModuleProjectDir());
        Assert.assertNull("MavenLifecycleListener -> afterSessionStart problem!", autoverSession.getConfig());
        Assert.assertTrue("MavenLifecycleListener -> afterSessionStart problem!", autoverSession.isDisable());
        Assert.assertFalse("MavenLifecycleListener -> afterSessionStart problem!", autoverSession.isDisablePomChange());

        //disabled = false
        //disabled pom change = true
        systemProperties.setProperty(MavenLifecycleListener.DISABLE_PROPERTY_KEY, Boolean.FALSE.toString());
        systemProperties.setProperty(MavenLifecycleListener.DISABLE_POM_CHANGE_PROPERTY_KEY, Boolean.TRUE.toString());
        mavenLifecycleListener.afterSessionStart(mavenSession);
        Assert.assertTrue("MavenLifecycleListener -> afterSessionStart problem!", multiModuleProjectDirectory.getCanonicalFile().equals(autoverSession.getMavenMultiModuleProjectDir()));
        Assert.assertTrue("MavenLifecycleListener -> afterSessionStart problem!", autoverConfigDecorator.equals(autoverSession.getConfig()));
        Assert.assertFalse("MavenLifecycleListener -> afterSessionStart problem!", autoverSession.isDisable());
        Assert.assertTrue("MavenLifecycleListener -> afterSessionStart problem!", autoverSession.isDisablePomChange());
    }

    @Test
    public void afterProjectsRead() throws IOException {
        final Map<String, File> newPomFiles = autoverSession.getNewPomFiles();

        //disabled = false
        //disabled pom change = false
        newPomFiles.clear();
        final File file11 = createTmpFile();
        newPomFiles.put(model1.getId(), file11);
        final File file21 = createTmpFile();
        newPomFiles.put(model2.getId(), file21);
        final File file31 = createTmpFile();
        newPomFiles.put(model3.getId(), file31);
        autoverSession.setDisable(false);
        autoverSession.setDisablePomChange(false);

        mavenLifecycleListener.afterProjectsRead(mavenSession);

        Assert.assertTrue("MavenLifecycleListener -> afterProjectsRead problem!", file11.equals(project1.getFile()));
        Assert.assertTrue("MavenLifecycleListener -> afterProjectsRead problem!", file21.equals(project2.getFile()));
        Assert.assertTrue("MavenLifecycleListener -> afterProjectsRead problem!", file31.equals(project3.getFile()));

        //disabled = true
        //disabled pom change = false
        newPomFiles.clear();
        final File file12 = createTmpFile();
        newPomFiles.put(model1.getId(), file12);
        final File file22 = createTmpFile();
        newPomFiles.put(model2.getId(), file22);
        final File file32 = createTmpFile();
        newPomFiles.put(model3.getId(), file32);
        autoverSession.setDisable(true);
        autoverSession.setDisablePomChange(false);

        mavenLifecycleListener.afterProjectsRead(mavenSession);

        Assert.assertTrue("MavenLifecycleListener -> afterProjectsRead problem!", file11.equals(project1.getFile()));
        Assert.assertTrue("MavenLifecycleListener -> afterProjectsRead problem!", file21.equals(project2.getFile()));
        Assert.assertTrue("MavenLifecycleListener -> afterProjectsRead problem!", file31.equals(project3.getFile()));

        //disabled = true
        //disabled pom change = true
        autoverSession.setDisable(true);
        autoverSession.setDisablePomChange(true);

        mavenLifecycleListener.afterProjectsRead(mavenSession);

        Assert.assertTrue("MavenLifecycleListener -> afterProjectsRead problem!", file11.equals(project1.getFile()));
        Assert.assertTrue("MavenLifecycleListener -> afterProjectsRead problem!", file21.equals(project2.getFile()));
        Assert.assertTrue("MavenLifecycleListener -> afterProjectsRead problem!", file31.equals(project3.getFile()));

        //disabled = false
        //disabled pom change = true
        autoverSession.setDisable(true);
        autoverSession.setDisablePomChange(true);

        mavenLifecycleListener.afterProjectsRead(mavenSession);

        Assert.assertTrue("MavenLifecycleListener -> afterProjectsRead problem!", file11.equals(project1.getFile()));
        Assert.assertTrue("MavenLifecycleListener -> afterProjectsRead problem!", file21.equals(project2.getFile()));
        Assert.assertTrue("MavenLifecycleListener -> afterProjectsRead problem!", file31.equals(project3.getFile()));

        //disabled = false
        //disabled pom change = false
        autoverSession.setDisable(false);
        autoverSession.setDisablePomChange(false);

        mavenLifecycleListener.afterProjectsRead(mavenSession);

        Assert.assertTrue("MavenLifecycleListener -> afterProjectsRead problem!", file12.equals(project1.getFile()));
        Assert.assertTrue("MavenLifecycleListener -> afterProjectsRead problem!", file22.equals(project2.getFile()));
        Assert.assertTrue("MavenLifecycleListener -> afterProjectsRead problem!", file32.equals(project3.getFile()));
    }

    @Test
    public void afterSessionEnd() throws IOException {
        final Map<String, File> newPomFiles = autoverSession.getNewPomFiles();

        newPomFiles.clear();
        final File file11 = createTmpFile();
        newPomFiles.put(model1.getId(), file11);
        final File file21 = createTmpFile();
        newPomFiles.put(model2.getId(), file21);
        final File file31 = createTmpFile();
        newPomFiles.put(model3.getId(), file31);
        final String tmpDir = System.getProperty("java.io.tmpdir"); // non null
        final File file42 = new File(tmpDir, System.currentTimeMillis() + "_" + System.nanoTime() + ".dummy");
        newPomFiles.put("DUMMY", file42);

        //disabled = true
        //disabled pom change = true
        autoverSession.setDisable(true);
        autoverSession.setDisablePomChange(true);

        mavenLifecycleListener.afterSessionEnd(mavenSession);

        Assert.assertTrue("MavenLifecycleListener -> afterSessionEnd problem!", file11.exists());
        Assert.assertTrue("MavenLifecycleListener -> afterSessionEnd problem!", file21.exists());
        Assert.assertTrue("MavenLifecycleListener -> afterSessionEnd problem!", file31.exists());

        //disabled = true
        //disabled pom change = false
        autoverSession.setDisable(true);
        autoverSession.setDisablePomChange(false);

        mavenLifecycleListener.afterSessionEnd(mavenSession);

        Assert.assertTrue("MavenLifecycleListener -> afterSessionEnd problem!", file11.exists());
        Assert.assertTrue("MavenLifecycleListener -> afterSessionEnd problem!", file21.exists());
        Assert.assertTrue("MavenLifecycleListener -> afterSessionEnd problem!", file31.exists());

        //disabled = false
        //disabled pom change = true
        autoverSession.setDisable(false);
        autoverSession.setDisablePomChange(true);

        mavenLifecycleListener.afterSessionEnd(mavenSession);

        Assert.assertTrue("MavenLifecycleListener -> afterSessionEnd problem!", file11.exists());
        Assert.assertTrue("MavenLifecycleListener -> afterSessionEnd problem!", file21.exists());
        Assert.assertTrue("MavenLifecycleListener -> afterSessionEnd problem!", file31.exists());

        //disabled = false
        //disabled pom change = false
        autoverSession.setDisable(false);
        autoverSession.setDisablePomChange(false);

        mavenLifecycleListener.afterSessionEnd(mavenSession);

        Assert.assertFalse("MavenLifecycleListener -> afterSessionEnd problem!", file11.exists());
        Assert.assertFalse("MavenLifecycleListener -> afterSessionEnd problem!", file21.exists());
        Assert.assertFalse("MavenLifecycleListener -> afterSessionEnd problem!", file31.exists());
    }
}