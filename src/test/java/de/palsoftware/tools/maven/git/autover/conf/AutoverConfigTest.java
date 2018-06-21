package de.palsoftware.tools.maven.git.autover.conf;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests getters and setters for {@link AutoverConfig}.
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class AutoverConfigTest {

    private AutoverConfig config;
    private AutoverBranchConfig branchConfig1;
    private AutoverBranchConfig branchConfig2;

    @Before
    public void setUp() {
        config = new AutoverConfig();
        branchConfig1 = new AutoverBranchConfig();
        branchConfig1.setNameRegex("AABBCC");
        branchConfig1.setStopOn(StopOnEnum.ON_FIRST);
        branchConfig2 = new AutoverBranchConfig();
        branchConfig2.setNameRegex("112233");
        branchConfig2.setStopOn(StopOnEnum.ON_FIRST_ANN);
    }

    @Test
    public void versionTagRegex() {
        String versionTagRegex = "AABBCC";
        config.setVersionTagRegex(versionTagRegex);
        Assert.assertTrue("AutoverConfig -> VersionTagRegex setter / getter problem!", versionTagRegex.equalsIgnoreCase(config.getVersionTagRegex()));
    }

    @Test
    public void getIncludeGroupIds() {
        final List<String> includeGroupIds = config.getIncludeGroupIds();
        includeGroupIds.add("AABBCC");
        Assert.assertTrue("AutoverConfig -> IncludeGroupIds setter / getter problem!", includeGroupIds.equals(config.getIncludeGroupIds()));

        final List<String> includeGroupIdsNew = new ArrayList<>();
        includeGroupIdsNew.add("AABBCC");
        Assert.assertTrue("AutoverConfig -> IncludeGroupIds setter / getter problem!", includeGroupIdsNew.equals(config.getIncludeGroupIds()));

        includeGroupIds.add("112233");
        includeGroupIdsNew.add("112233");
        Assert.assertTrue("AutoverConfig -> IncludeGroupIds setter / getter problem!", includeGroupIdsNew.equals(config.getIncludeGroupIds()));
    }

    @Test
    public void getAutoverBranchConfigs() {
        final List<AutoverBranchConfig> autoverBranchConfigs = config.getAutoverBranchConfigs();
        autoverBranchConfigs.add(branchConfig1);
        Assert.assertTrue("AutoverConfig -> AutoverBranchConfigs setter / getter problem!", autoverBranchConfigs.equals(config.getAutoverBranchConfigs()));

        final List<AutoverBranchConfig> autoverBranchConfigsNew = new ArrayList<>();
        autoverBranchConfigsNew.add(branchConfig1);
        Assert.assertTrue("AutoverConfig -> AutoverBranchConfigs setter / getter problem!", autoverBranchConfigsNew.equals(config.getAutoverBranchConfigs()));

        autoverBranchConfigs.add(branchConfig2);
        autoverBranchConfigsNew.add(branchConfig2);
        Assert.assertTrue("AutoverConfig -> AutoverBranchConfigs setter / getter problem!", autoverBranchConfigsNew.equals(config.getAutoverBranchConfigs()));
    }

    @Test
    public void toStringTest() {
        config.setVersionTagRegex("AABBCC");
        config.getIncludeGroupIds().add("123456");
        final String toString1 = config.toString();
        config.setVersionTagRegex("ABCDEF");
        final String toString2 = config.toString();

        Assert.assertFalse("AutoverConfig ->  toString problem!", toString1.equals(toString2));
    }

    @Test
    public void equalsAndHashCode() {
        final AutoverConfig config1 = new AutoverConfig();
        config1.setVersionTagRegex("AABBCC");
        final AutoverBranchConfig branchConfig1 = new AutoverBranchConfig();
        branchConfig1.setNameRegex("ABCDEF");
        branchConfig1.setStopOn(StopOnEnum.ON_FIRST);
        config1.getAutoverBranchConfigs().add(branchConfig1);
        config1.getIncludeGroupIds().add("aaBBccDD");

        final AutoverConfig config2 = new AutoverConfig();
        config2.setVersionTagRegex("AABBCC");
        final AutoverBranchConfig branchConfig2 = new AutoverBranchConfig();
        branchConfig2.setNameRegex("ABCDEF");
        branchConfig2.setStopOn(StopOnEnum.ON_FIRST);
        config2.getAutoverBranchConfigs().add(branchConfig2);
        config2.getIncludeGroupIds().add("aaBBccDD");

        final AutoverConfig config3 = new AutoverConfig();
        config3.setVersionTagRegex("AABBCC");
        final AutoverBranchConfig branchConfig3 = new AutoverBranchConfig();
        branchConfig3.setNameRegex("ABCaaF");
        branchConfig3.setStopOn(StopOnEnum.ON_FIRST);
        config3.getAutoverBranchConfigs().add(branchConfig3);
        config3.getIncludeGroupIds().add("aaBB11DD");

        //equals
        Assert.assertTrue("AutoverConfig -> equals problem!", config1.equals(config1));
        Assert.assertTrue("AutoverConfig -> equals problem!", config1.equals(config2));
        Assert.assertFalse("AutoverConfig -> equals problem!", config1.equals(null));
        Assert.assertFalse("AutoverConfig -> equals problem!", config1.equals(new Object()));
        Assert.assertFalse("AutoverConfig -> equals problem!", config1.equals(config3));

        //hashCode
        Assert.assertTrue("AutoverConfig -> hashCode problem!", config1.hashCode() == config1.hashCode());
        Assert.assertTrue("AutoverConfig -> hashCode problem!", config1.hashCode() == config2.hashCode());
        Assert.assertFalse("AutoverConfig -> hashCode problem!", config1.hashCode() == config3.hashCode());
    }
}