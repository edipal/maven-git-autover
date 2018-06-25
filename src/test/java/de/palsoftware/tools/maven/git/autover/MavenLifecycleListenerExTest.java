package de.palsoftware.tools.maven.git.autover;

import de.palsoftware.tools.maven.git.autover.conf.AutoverConfig;
import org.apache.maven.MavenExecutionException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Properties;

/**
 * Test class for {@link MavenLifecycleListener}
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class MavenLifecycleListenerExTest extends MavenBaseTest {

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

        final URL configFileUrl = this.getClass().getClassLoader().getResource("test_config_bad.xml");
        final File configFile = new File(configFileUrl.toURI());
        final Properties systemProperties = mavenSession.getSystemProperties();
        systemProperties.setProperty("git.autover.conf", configFile.getAbsolutePath());


    }

    @Test(expected = MavenExecutionException.class)
    public void afterSessionStart() throws MavenExecutionException {
        mavenLifecycleListener.afterSessionStart(mavenSession);
    }
}