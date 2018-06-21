package de.palsoftware.tools.maven.git.autover;

import com.sun.xml.bind.v2.ContextFactory;
import de.palsoftware.tools.maven.git.autover.conf.AutoverConfig;
import org.codehaus.plexus.logging.Logger;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;

/**
 * Used to read the maven extension configuration.
 * It will try in this order:
 * 1. command line (-Dgit.autover.conf)
 * 2. .mvn folder (git.autover.conf.xml)
 * 3. classpath (default.git.autover.conf.xml)
 * If no configuration is found, a default configuration (provided by the {@link ConfigHelper})
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class ConfigReader {

    /**
     * the default configuration file name.
     */
    private static final String DEFAULT_GIT_AUTOVER_CONF_XML = "default.git.autover.conf.xml";
    /**
     * the configuration file name.
     */
    private static final String GIT_AUTOVER_CONF_XML = "git.autover.conf.xml";
    /**
     * the mvn folder name.
     */
    private static final String MVN_FOLDER_NAME = ".mvn";
    /**
     * the git autover conf property.
     */
    private static final String GIT_AUTOVER_CONF_PROPERTY = "git.autover.conf";
    /**
     * The logger.
     */
    private Logger logger;

    /**
     * Read the configuration to be used or return the default configuration.
     *
     * @param systemProperties      the system properties - from maven session
     * @param multiModuleProjectDir the module project dir - from maven session
     * @return the configuration
     * @throws JAXBException xml parsing problem
     * @throws SAXException  xml parsing problem
     * @throws IOException   for io problems
     */
    public AutoverConfigDecorator readConfig(final Properties systemProperties, final File multiModuleProjectDir) throws IOException, JAXBException,
            SAXException {
        InputStream schemaInputStream = null;
        InputStream configInputStream = null;
        try {
            final String configPackageName = AutoverConfig.class.getPackage().getName();
            //JAXBContext.newInstance(AutoverConfig.class);
            //for some reason (classloader related??) maven can't find the right context factory
            final JAXBContext jc = ContextFactory.createContext(new Class[]{AutoverConfig.class}, Collections.emptyMap());

            final Unmarshaller u = jc.createUnmarshaller();

            //compute schema
            final ClassLoader classLoader = this.getClass().getClassLoader();
            final String schemaPath = configPackageName.replaceAll("\\.", "\\/") + "/config.xsd";
            schemaInputStream = classLoader.getResourceAsStream(schemaPath);
            final Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new StreamSource(schemaInputStream));
            u.setSchema(schema);

            //get conf file
            //1st from command line
            final String configPath = systemProperties.getProperty(GIT_AUTOVER_CONF_PROPERTY);
            if ((configPath != null) && (configPath.length() > 0)) {
                final File configFile = new File(configPath);
                if (configFile.exists() && configFile.isFile()) {
                    configInputStream = new FileInputStream(configFile);
                    if (logger.isDebugEnabled()) {
                        logger.debug(new LocalizationHelper().getMessage(LocalizationHelper.MSG_CONF_FILE_USER, configFile.getAbsolutePath()));
                    }
                } else {
                    throw new RuntimeException(new LocalizationHelper().getMessage(LocalizationHelper.ERR_CONF_FILE_INVALID, configPath));
                }
            }
            //2nd from mvn folder
            if (configInputStream == null) {
                final File mvnDir = new File(multiModuleProjectDir, MVN_FOLDER_NAME);
                if (mvnDir.exists() && !mvnDir.isFile()) {
                    final File configFile = new File(mvnDir, GIT_AUTOVER_CONF_XML);
                    if (configFile.exists() && configFile.isFile()) {
                        configInputStream = new FileInputStream(configFile);
                        if (logger.isDebugEnabled()) {
                            logger.debug(new LocalizationHelper().getMessage(LocalizationHelper.MSG_CONF_FILE_USER, configFile.getAbsolutePath()));
                        }
                    }
                }
            }
            //3rd from classpath
            if (configInputStream == null) {
                configInputStream = classLoader.getResourceAsStream(DEFAULT_GIT_AUTOVER_CONF_XML);
                if (configInputStream != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(new LocalizationHelper().getMessage(LocalizationHelper.MSG_CONF_FILE_USER, DEFAULT_GIT_AUTOVER_CONF_XML));
                    }
                }
            }
            AutoverConfig config = null;
            if (configInputStream != null) {
                final JAXBElement<AutoverConfig> configElement = u.unmarshal(new StreamSource(configInputStream), AutoverConfig.class);
                config = configElement.getValue();
            }

            //if a conf file was not found, use default values
            if (config == null) {
                config = ConfigHelper.getDefaultConfiguration();
            }
            return new AutoverConfigDecorator(config);
        } finally {
            if (schemaInputStream != null) {
                try {
                    schemaInputStream.close();
                } catch (final IOException e) {
                    //ignore
                }
            }
            if (configInputStream != null) {
                try {
                    configInputStream.close();
                } catch (final IOException e) {
                    //ignore
                }
            }
        }
    }

    /**
     * Setter.
     *
     * @param value the logger
     */
    public void setLogger(final Logger value) {
        this.logger = value;
    }
}
