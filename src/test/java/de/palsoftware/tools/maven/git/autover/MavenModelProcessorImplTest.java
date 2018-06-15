package de.palsoftware.tools.maven.git.autover;

import de.palsoftware.tools.maven.git.autover.conf.AutoverConfig;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelProcessor;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.model.io.DefaultModelReader;
import org.apache.maven.model.locator.DefaultModelLocator;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: comment
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class MavenModelProcessorImplTest extends BaseTest {

    private MavenModelProcessorImpl mavenModelProcessor;
    private File testPomFile;
    private Map<String, Object> options;

    @Override
    @Before
    public void setUp() throws Exception {
        mavenModelProcessor = new MavenModelProcessorImpl();
        final AutoverSession autoverSession = new AutoverSession();
        final AutoverConfig autoverConfig = new AutoverConfig();
        autoverConfig.setVersionTagRegex("AABBCC");
        final AutoverConfigDecorator autoverConfigDecorator = new AutoverConfigDecorator(autoverConfig);
        autoverSession.setConfig(autoverConfigDecorator);
        autoverSession.setMavenMultiModuleProjectDir(getFolder("empty"));
        mavenModelProcessor.setAutoverSession(autoverSession);
        mavenModelProcessor.setModelReader(new DefaultModelReader());
        mavenModelProcessor.setModelLocator(new DefaultModelLocator());
        mavenModelProcessor.setLogger(new ConsoleLogger());

        final URL testPomURL = this.getClass().getClassLoader().getResource("test_pom.xml");
        options = new HashMap<>();
        options.put(ModelProcessor.IS_STRICT, true);
        testPomFile = new File(testPomURL.toURI());
        ModelSource modelSource = new FileModelSource(testPomFile);
        options.put(ModelProcessor.SOURCE, modelSource);
    }

    @Test
    public void read1() throws IOException {
        final Model testPomModel = mavenModelProcessor.read(testPomFile, new HashMap<>());
        assertModel(testPomModel);
    }

    @Test
    public void read2() throws IOException {
        final Model testPomModel = mavenModelProcessor.read(new FileReader(testPomFile), new HashMap<>());
        assertModel(testPomModel);
    }

    @Test
    public void read3() throws IOException {
        final Model testPomModel = mavenModelProcessor.read(new FileInputStream(testPomFile), new HashMap<>());
        assertModel(testPomModel);
    }

    private void assertModel(final Model testPomModel) {
        Assert.assertNotNull("MavenModelProcessorImpl -> read (File, Map) problem!", testPomModel);
        Assert.assertTrue("MavenModelProcessorImpl -> read (File, Map) problem!", "de.palsoftware.tools".equals(testPomModel.getGroupId()));
        Assert.assertTrue("MavenModelProcessorImpl -> read (File, Map) problem!", "maven-git-autover".equals(testPomModel.getArtifactId()));
    }
}