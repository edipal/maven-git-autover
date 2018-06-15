package de.palsoftware.tools.maven.git.autover;

import de.palsoftware.tools.maven.git.autover.conf.AutoverConfig;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelProcessor;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.model.io.DefaultModelReader;
import org.apache.maven.model.locator.DefaultModelLocator;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.junit.RepositoryTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Main test case for {@link GitHelper}
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class MavenHelperIntegrationTest extends RepositoryTestCase {

    private AutoverSession autoverSession;
    private Map<String, Object> options;
    private Model model;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        //prepare git repo

        //   o file2 commit
        //   |
        //   o file1 commit (1.0.0-SNAPSHOT - not annotated)

        final Git git = new Git(db);

        //initial commit
        git.commit().setMessage("initial commit").call();

        //file 1 - master
        writeTrashFile("file1", "file1content");
        git.add().addFilepattern("file1").call();
        git.commit().setMessage("file1 commit").call();

        //tag 1.0.0-SNAPSHOT (not annotated)
        git.tag().setAnnotated(false).setName("1.0.0-SNAPSHOT").call();

        //file 2 - master
        writeTrashFile("file2", "file2content");
        git.add().addFilepattern("file2").call();
        git.commit().setMessage("file2 commit").call();

        //pom.xml
        final URL testPom2URL = this.getClass().getClassLoader().getResource("test_pom2.xml");
        final File testPom2File = new File(testPom2URL.toURI());

        final String testPom2FileContent = read(testPom2File);
        writeTrashFile("pom.xml", testPom2FileContent);
        git.add().addFilepattern("pom.xml").call();
        git.commit().setMessage("pom.xml commit").call();

        //prepare maven model
        autoverSession = new AutoverSession();
        final AutoverConfig config = ConfigHelper.getDefaultConfiguration();
        final AutoverConfigDecorator configDecorator = new AutoverConfigDecorator(config);
        autoverSession.setConfig(configDecorator);
        final File gitRepoDir = git.getRepository().getWorkTree();
        autoverSession.setMavenMultiModuleProjectDir(gitRepoDir);
        MavenModelProcessorImpl mavenModelProcessor = new MavenModelProcessorImpl();
        mavenModelProcessor.setAutoverSession(autoverSession);
        mavenModelProcessor.setModelReader(new DefaultModelReader());
        mavenModelProcessor.setModelLocator(new DefaultModelLocator());
        mavenModelProcessor.setLogger(new ConsoleLogger());

        options = new HashMap<>();
        options.put(ModelProcessor.IS_STRICT, true);
        final File modelFile = new File(gitRepoDir, "pom.xml");
        ModelSource modelSource = new FileModelSource(modelFile);
        options.put(ModelProcessor.SOURCE, modelSource);

        model = mavenModelProcessor.read(modelFile, new HashMap<>());
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }


    @Test
    public void setAutoVersion() throws IOException {

        model.setVersion("0.0.0-SNAPSHOT");

        //test
        final MavenHelper mh = new MavenHelper();
        mh.setLogger(new ConsoleLogger());
        mh.setAutoVersion(model, options, autoverSession);

        Assert.assertTrue("1.0.0-SNAPSHOT".equals(model.getVersion()));
        final String modelId = model.getId();
        final Map<String, File> newPomFiles = autoverSession.getNewPomFiles();
        final File newPomFile = newPomFiles.get(modelId);
        Assert.assertNotNull(newPomFile);
        Assert.assertTrue(newPomFile.getName().startsWith("mvn_git_autover_pom"));
    }
}