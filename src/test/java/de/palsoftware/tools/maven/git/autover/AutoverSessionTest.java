package de.palsoftware.tools.maven.git.autover;

import de.palsoftware.tools.maven.git.autover.conf.AutoverBranchConfig;
import de.palsoftware.tools.maven.git.autover.conf.AutoverConfig;
import de.palsoftware.tools.maven.git.autover.conf.StopOnEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

/**
 * Simple getter and setter tests for {@link AutoverSession}
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class AutoverSessionTest {

    private AutoverSession autoverSession;

    @Before
    public void setUp() {
        autoverSession = new AutoverSession();
    }

    @Test
    public void mavenMultiModuleProjectDir() {
        File file = new File("c:/", "ttt.txt");
        autoverSession.setMavenMultiModuleProjectDir(file);
        Assert.assertTrue("AutoverSession -> MavenMultiModuleProjectDir setter / getter problem!", file.equals(autoverSession.getMavenMultiModuleProjectDir()));

        file = new File("c:/", "bbb.txt");
        autoverSession.setMavenMultiModuleProjectDir(file);
        Assert.assertTrue("AutoverSession -> MavenMultiModuleProjectDir setter / getter problem!", file.equals(autoverSession.getMavenMultiModuleProjectDir()));
    }

    @Test
    public void getConfig() {
        AutoverConfig config = new AutoverConfig();
        config.setVersionTagRegex("AABBCC");
        AutoverConfigDecorator configDecorator = new AutoverConfigDecorator(config);
        autoverSession.setConfig(configDecorator);

        Assert.assertTrue("AutoverSession -> Config setter / getter problem!", configDecorator.equals(autoverSession.getConfig()));

        config = new AutoverConfig();
        config.setVersionTagRegex("112233");
        configDecorator = new AutoverConfigDecorator(config);
        autoverSession.setConfig(configDecorator);
        Assert.assertTrue("AutoverSession -> Config setter / getter problem!", configDecorator.equals(autoverSession.getConfig()));
    }

    @Test
    public void newPomFiles() {
        final Map<String, File> map = autoverSession.getNewPomFiles();
        map.put("112233", new File("123456", "AABBCC.txt"));
        Assert.assertTrue("AutoverSession -> NewPomFiles setter / getter problem!", map.equals(autoverSession.getNewPomFiles()));

        map.put("112233", new File("123456", "AABBDD.txt"));
        Assert.assertTrue("AutoverSession -> NewPomFiles setter / getter problem!", map.equals(autoverSession.getNewPomFiles()));
    }

    @Test
    public void equalsAndHashCode() {
        final AutoverSession session1 = new AutoverSession();
        final AutoverConfig autoverConfig1 = new AutoverConfig();
        autoverConfig1.setVersionTagRegex("AABBCC");
        autoverConfig1.getIncludeGroupIds().add("123456");
        final AutoverBranchConfig autoverBranchConfig1 = new AutoverBranchConfig();
        autoverBranchConfig1.setStopOn(StopOnEnum.ON_FIRST);
        autoverBranchConfig1.setNameRegex("abcdef");
        autoverConfig1.getAutoverBranchConfigs().add(autoverBranchConfig1);
        session1.setConfig(new AutoverConfigDecorator(autoverConfig1));
        session1.setMavenMultiModuleProjectDir(new File("aa", "bb"));

        final AutoverSession session2 = new AutoverSession();
        session2.setConfig(new AutoverConfigDecorator(autoverConfig1));
        session2.setMavenMultiModuleProjectDir(new File("aa", "bb"));

        final AutoverSession session3 = new AutoverSession();
        final AutoverConfig autoverConfig3 = new AutoverConfig();
        autoverConfig3.setVersionTagRegex("AABBCC11");
        autoverConfig3.getIncludeGroupIds().add("12345611");
        final AutoverBranchConfig autoverBranchConfig3 = new AutoverBranchConfig();
        autoverBranchConfig3.setStopOn(StopOnEnum.ON_FIRST_ANN);
        autoverBranchConfig3.setNameRegex("abcdef11");
        autoverConfig3.getAutoverBranchConfigs().add(autoverBranchConfig3);
        session3.setConfig(new AutoverConfigDecorator(autoverConfig3));
        session3.setMavenMultiModuleProjectDir(new File("aa11", "bb11"));

        //equals
        Assert.assertTrue("AutoverSession -> equals problem!", session1.equals(session1));
        Assert.assertTrue("AutoverSession -> equals problem!", session1.equals(session2));
        Assert.assertFalse("AutoverSession -> equals problem!", session1.equals(null));
        Assert.assertFalse("AutoverSession -> equals problem!", session1.equals(new Object()));
        Assert.assertFalse("AutoverSession -> equals problem!", session1.equals(session3));

        //hashCode
        Assert.assertTrue("AutoverSession -> hashCode problem!", session1.hashCode() == session1.hashCode());
        Assert.assertTrue("AutoverSession -> hashCode problem!", session1.hashCode() == session2.hashCode());
        Assert.assertFalse("AutoverSession -> hashCode problem!", session1.hashCode() == session3.hashCode());
    }
}