package de.palsoftware.tools.maven.git.autover;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * TODO: comment
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class MavenHelperTest extends BaseTest {

    private MavenHelper mavenHelper;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        mavenHelper = new MavenHelper();
    }

    @Test
    public void replacePomFile() throws IOException {
        File tmpFile = createTmpFile();
        final MavenProject mavenProject = new MavenProject();
        mavenHelper.replacePomFile(mavenProject, tmpFile);
        Assert.assertTrue("MavenHelper -> replacePomFile problem!", tmpFile.equals(mavenProject.getFile()));
    }

    @Test
    public void writeNewPomFile() throws IOException, XmlPullParserException {
        InputStream testPomInputStreamInitial = null;
        InputStream testPomInputStreamNew = null;
        try {
            testPomInputStreamInitial = this.getClass().getClassLoader().getResourceAsStream("test_pom.xml");
            final Model testModelInitial = new MavenXpp3Reader().read(testPomInputStreamInitial);
            final File tmpFile = createTmpFile();
            final File newPomFile = mavenHelper.writeNewPomFile(testModelInitial, tmpFile.getParentFile());
            testPomInputStreamNew = new FileInputStream(newPomFile);
            final Model testModelNew = new MavenXpp3Reader().read(testPomInputStreamNew);
            //model has no equals
            Assert.assertTrue("MavenHelper -> writeNewPomFile problem!", testModelInitial.getId().equals(testModelNew.getId()));
        } finally {
            if (testPomInputStreamInitial != null) {
                testPomInputStreamInitial.close();
            }
            if (testPomInputStreamNew != null) {
                testPomInputStreamNew.close();
            }
        }
    }
}