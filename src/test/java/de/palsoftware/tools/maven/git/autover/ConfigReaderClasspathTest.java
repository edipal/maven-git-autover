package de.palsoftware.tools.maven.git.autover;

import de.palsoftware.tools.maven.git.autover.conf.AutoverConfig;
import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Tests for the {@link ConfigReader}
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class ConfigReaderClasspathTest extends BaseTest {

    private File defConfCopy;

    @Before
    public void setUp() throws Exception {
        File defConfFolder = getFolder("def_conf");
        File defConf = new File(defConfFolder, "default.git.autover.conf.xml");
        defConfCopy = new File(defConfFolder.getParent(), "default.git.autover.conf.xml");
        FileUtils.copyFile(defConf, defConfCopy);
    }

    @After
    public void tearDown() {
        defConfCopy.delete();
    }

    @Test
    public void readConfigClasspath() throws URISyntaxException, IOException, JAXBException, SAXException {
        final File emptyFolder = getFolder("empty");
        final Properties systemProperties = new Properties();

        final ConfigReader configReader = new ConfigReader();
        configReader.setLogger(new ConsoleLogger());
        final AutoverConfigDecorator readConfig = configReader.readConfig(systemProperties, emptyFolder);
        final AutoverConfig config = readConfig("def_conf/default.git.autover.conf.xml");

        compareConfigs(readConfig, config);
    }
}