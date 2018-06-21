package de.palsoftware.tools.maven.git.autover;

import com.sun.xml.bind.v2.ContextFactory;
import de.palsoftware.tools.maven.git.autover.conf.AutoverBranchConfig;
import de.palsoftware.tools.maven.git.autover.conf.AutoverConfig;
import org.junit.Assert;
import org.junit.Before;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * Base class for tests.
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public abstract class BaseTest {

    private Random random;

    @Before
    public void setUp() throws Exception {
        random = new Random();
    }

    protected File getFolder(final String folder) throws URISyntaxException {
        final URL folderUrl = this.getClass().getClassLoader().getResource(folder);
        final File folderFile = new File(folderUrl.toURI());
        return folderFile;
    }

    protected Properties getPropertiesWithConfig() throws URISyntaxException {
        final URL configFileUrl = this.getClass().getClassLoader().getResource("test_config.xml");
        final File configFile = new File(configFileUrl.toURI());
        final Properties systemProperties = new Properties();
        systemProperties.setProperty("git.autover.conf", configFile.getAbsolutePath());
        return systemProperties;
    }

    protected void compareConfigs(AutoverConfigDecorator readConfig, AutoverConfig defaultConfig) {
        Assert.assertTrue("ConfigReader problem!", readConfig.getVersionTagRegex().equals(defaultConfig.getVersionTagRegex()));
        Assert.assertTrue("ConfigReader problem!", readConfig.getIncludeGroupIds().equals(defaultConfig.getIncludeGroupIds()));
        final List<AutoverBranchConfigDecorator> readBranchConfigs = readConfig.getAutoverBranchConfigs();
        final List<AutoverBranchConfig> defaultBranchConfigs = defaultConfig.getAutoverBranchConfigs();
        Assert.assertTrue("ConfigReader problem!", readBranchConfigs.size() == defaultBranchConfigs.size());
        //the order should be the same in both
        for (int i = 0; i < readBranchConfigs.size(); i++) {
            final AutoverBranchConfigDecorator readBranchConfig = readBranchConfigs.get(i);
            final AutoverBranchConfig defaultBranchConfig = defaultBranchConfigs.get(i);
            Assert.assertTrue("ConfigReader problem!", readBranchConfig.getNameRegex().equals(defaultBranchConfig.getNameRegex()));
            Assert.assertTrue("ConfigReader problem!", readBranchConfig.getStopOn() == defaultBranchConfig.getStopOn());
        }
    }

    protected AutoverConfig readConfig(String filePath) throws JAXBException, SAXException {
        final String configPackageName = AutoverConfig.class.getPackage().getName();
        final JAXBContext jc = ContextFactory.createContext(new Class[]{AutoverConfig.class}, Collections.emptyMap());
        Unmarshaller u = jc.createUnmarshaller();
        final ClassLoader classLoader = this.getClass().getClassLoader();
        final String schemaPath = configPackageName.replaceAll("\\.", "\\/") + "/config.xsd";
        final InputStream schemaInputStream = classLoader.getResourceAsStream(schemaPath);
        final Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new StreamSource(schemaInputStream));
        u.setSchema(schema);

        final InputStream xmlInputStream = classLoader.getResourceAsStream(filePath);
        final JAXBElement<AutoverConfig> configElement = u.unmarshal(new StreamSource(xmlInputStream), AutoverConfig.class);
        return configElement.getValue();
    }

    protected File createTmpFile() throws IOException {
        final String suffix = String.valueOf(System.currentTimeMillis()) + "_" + String.valueOf(Math.abs(random.nextInt()));
        final File tmpFile = File.createTempFile("tmp_maven_git_autover_", suffix);
        tmpFile.deleteOnExit();
        return tmpFile;
    }
}
