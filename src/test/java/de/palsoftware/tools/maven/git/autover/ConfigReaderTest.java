package de.palsoftware.tools.maven.git.autover;

import de.palsoftware.tools.maven.git.autover.conf.AutoverConfig;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

/**
 * Tests for the {@link ConfigReader}
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class ConfigReaderTest extends BaseTest {

    @Test
    public void readConfigDefault() throws URISyntaxException, JAXBException, IOException, SAXException {
        final File emptyFolder = getFolder("empty");
        final Properties systemProperties = new Properties();

        final ConfigReader configReader = new ConfigReader();
        final ConsoleLogger logger = new ConsoleLogger();
        logger.setThreshold(Logger.LEVEL_DEBUG);
        configReader.setLogger(logger);
        final AutoverConfigDecorator readConfig = configReader.readConfig(systemProperties, emptyFolder);
        final AutoverConfig defaultConfig = ConfigHelper.getDefaultConfiguration();

        compareConfigs(readConfig, defaultConfig);
    }

    @Test
    public void readConfigCmdLine() throws URISyntaxException, JAXBException, SAXException, IOException {
        final File emptyFolder = getFolder("empty");
        final Properties systemProperties = getPropertiesWithConfig();

        final ConfigReader configReader = new ConfigReader();
        final ConsoleLogger logger = new ConsoleLogger();
        logger.setThreshold(Logger.LEVEL_DEBUG);
        configReader.setLogger(logger);
        final AutoverConfigDecorator readConfig = configReader.readConfig(systemProperties, emptyFolder);
        final AutoverConfig config = readConfig("test_config.xml");

        compareConfigs(readConfig, config);
    }

    @Test
    public void readConfigCmdLineExDoesNotExist() throws URISyntaxException {
        final File emptyFolder = getFolder("empty");
        final Properties systemProperties = new Properties();
        systemProperties.setProperty("git.autover.conf", "xsaassasdsa" + System.currentTimeMillis() + ".aaa");

        try {
            final ConfigReader configReader = new ConfigReader();
            final ConsoleLogger logger = new ConsoleLogger();
            logger.setThreshold(Logger.LEVEL_DEBUG);
            configReader.setLogger(logger);
            configReader.readConfig(systemProperties, emptyFolder);
            Assert.fail("ConfigReader problem!");
        } catch (final Exception e) {
            //ok
        }
    }

    @Test
    public void readConfigCmdLineExBad() throws URISyntaxException {
        final File emptyFolder = getFolder("empty");
        final URL configFileUrl = this.getClass().getClassLoader().getResource("test_config_bad.xml");
        final File configFile = new File(configFileUrl.toURI());
        final Properties systemProperties = new Properties();
        systemProperties.setProperty("git.autover.conf", configFile.getAbsolutePath());

        try {
            final ConfigReader configReader = new ConfigReader();
            final ConsoleLogger logger = new ConsoleLogger();
            logger.setThreshold(Logger.LEVEL_DEBUG);
            configReader.setLogger(logger);
            configReader.readConfig(systemProperties, emptyFolder);
            Assert.fail("ConfigReader problem!");
        } catch (final Exception e) {
            //ok
        }
    }

    @Test
    public void readConfigMvnFolder() throws URISyntaxException, JAXBException, SAXException, IOException {
        final File emptyFolder = getFolder("test_maven");
        final Properties systemProperties = new Properties();

        final ConfigReader configReader = new ConfigReader();
        final ConsoleLogger logger = new ConsoleLogger();
        logger.setThreshold(Logger.LEVEL_DEBUG);
        configReader.setLogger(logger);
        final AutoverConfigDecorator readConfig = configReader.readConfig(systemProperties, emptyFolder);
        final AutoverConfig config = readConfig("test_maven/.mvn/git.autover.conf.xml");

        compareConfigs(readConfig, config);
    }
}