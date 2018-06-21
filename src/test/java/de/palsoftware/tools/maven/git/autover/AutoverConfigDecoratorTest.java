package de.palsoftware.tools.maven.git.autover;

import de.palsoftware.tools.maven.git.autover.conf.AutoverBranchConfig;
import de.palsoftware.tools.maven.git.autover.conf.AutoverConfig;
import de.palsoftware.tools.maven.git.autover.conf.StopOnEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests getters and setters for {@link AutoverBranchConfigDecorator}.
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class AutoverConfigDecoratorTest {

    private AutoverConfig config;
    private AutoverBranchConfig branchConfig1;
    private AutoverBranchConfig branchConfig2;

    @Before
    public void setUp() {
        config = new AutoverConfig();
        config.setVersionTagRegex("AABBCC");
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
        final AutoverConfigDecorator autoverConfigDecorator = new AutoverConfigDecorator(config);
        Assert.assertTrue("AutoverConfigDecorator -> VersionTagRegex setter / getter problem!", versionTagRegex.equalsIgnoreCase(autoverConfigDecorator.getVersionTagRegex()));

        versionTagRegex = "112233";
        config.setVersionTagRegex(versionTagRegex);
        Assert.assertTrue("AutoverConfigDecorator -> VersionTagRegex setter / getter problem!", versionTagRegex.equalsIgnoreCase(autoverConfigDecorator.getVersionTagRegex()));
    }

    @Test
    public void versionTagPattern() {
        String versionTagRegex = "AABBCC";
        config.setVersionTagRegex(versionTagRegex);
        final AutoverConfigDecorator autoverConfigDecorator = new AutoverConfigDecorator(config);
        Assert.assertTrue("AutoverConfigDecorator -> VersionTagRegex setter / getter problem!", versionTagRegex.equalsIgnoreCase(autoverConfigDecorator.getVersionTagPattern().pattern()));
    }

    @Test
    public void includeGroupIds() {
        final AutoverConfigDecorator autoverConfigDecorator = new AutoverConfigDecorator(config);
        final List<String> includeGroupIds = config.getIncludeGroupIds();
        includeGroupIds.add("AABBCC");
        Assert.assertTrue("autoverConfigDecorator -> IncludeGroupIds setter / getter problem!", includeGroupIds.equals(autoverConfigDecorator.getIncludeGroupIds()));

        final List<String> includeGroupIdsNew = new ArrayList<>();
        includeGroupIdsNew.add("AABBCC");
        Assert.assertTrue("autoverConfigDecorator -> IncludeGroupIds setter / getter problem!", includeGroupIdsNew.equals(autoverConfigDecorator.getIncludeGroupIds()));

        includeGroupIds.add("112233");
        includeGroupIdsNew.add("112233");
        Assert.assertTrue("autoverConfigDecorator -> IncludeGroupIds setter / getter problem!", includeGroupIdsNew.equals(autoverConfigDecorator.getIncludeGroupIds()));
    }

    @Test
    public void autoverBranchConfigs() {
        final List<AutoverBranchConfig> autoverBranchConfigs = config.getAutoverBranchConfigs();
        autoverBranchConfigs.add(branchConfig1);
        AutoverConfigDecorator autoverConfigDecorator = new AutoverConfigDecorator(config);
        final List<AutoverBranchConfigDecorator> branchConfigsDecorators = autoverConfigDecorator.getAutoverBranchConfigs();
        Assert.assertTrue("AutoverConfigDecorator -> AutoverBranchConfigs setter / getter problem!", (autoverBranchConfigs.size() == 1) && (branchConfigsDecorators.size() == 1));

        final AutoverBranchConfig branchConfig = autoverBranchConfigs.get(0);
        final AutoverBranchConfigDecorator branchConfigDecorator = branchConfigsDecorators.get(0);
        Assert.assertTrue("AutoverConfigDecorator -> AutoverBranchConfigs setter / getter problem!", branchConfig.getNameRegex().equals(branchConfigDecorator.getNameRegex()));
        Assert.assertTrue("AutoverConfigDecorator -> AutoverBranchConfigs setter / getter problem!", branchConfig.getStopOn() == branchConfigDecorator.getStopOn());
    }

    @Test
    public void toStringTest() {
        final AutoverConfig config1 = new AutoverConfig();
        config1.setVersionTagRegex("AABBCC");
        final AutoverBranchConfig branchConfig1 = new AutoverBranchConfig();
        branchConfig1.setNameRegex("ABCDEF");
        branchConfig1.setStopOn(StopOnEnum.ON_FIRST);
        config1.getAutoverBranchConfigs().add(branchConfig1);
        config1.getIncludeGroupIds().add("aaBBccDD");
        final AutoverConfigDecorator configDecorator1 = new AutoverConfigDecorator(config1);
        final AutoverConfigDecorator configDecorator2 = new AutoverConfigDecorator(config1);
        final String toString1 = configDecorator1.toString();
        final String toString2 = configDecorator2.toString();

        Assert.assertTrue("AutoverConfigDecorator ->  toString problem!", toString1.equals(toString2));
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
        final AutoverConfigDecorator configDecorator1 = new AutoverConfigDecorator(config1);
        final AutoverConfigDecorator configDecorator2 = new AutoverConfigDecorator(config1);
        final AutoverConfig config2 = new AutoverConfig();
        config2.setVersionTagRegex("AABBCC");
        final AutoverBranchConfig branchConfig2 = new AutoverBranchConfig();
        branchConfig2.setNameRegex("AABBCC");
        branchConfig2.setStopOn(StopOnEnum.ON_FIRST_ANN);
        config2.getAutoverBranchConfigs().add(branchConfig2);
        config2.getIncludeGroupIds().add("112233");
        final AutoverConfigDecorator configDecorator3 = new AutoverConfigDecorator(config2);

        //equals
        Assert.assertTrue("AutoverConfigDecorator -> equals problem!", configDecorator1.equals(configDecorator1));
        Assert.assertTrue("AutoverConfigDecorator -> equals problem!", configDecorator1.equals(configDecorator2));
        Assert.assertFalse("AutoverConfigDecorator -> equals problem!", configDecorator1.equals(null));
        Assert.assertFalse("AutoverConfigDecorator -> equals problem!", configDecorator1.equals(new Object()));
        Assert.assertFalse("AutoverConfigDecorator -> equals problem!", configDecorator1.equals(configDecorator3));

        //hashCode
        Assert.assertTrue("AutoverConfigDecorator -> hashCode problem!", configDecorator1.hashCode() == configDecorator1.hashCode());
        Assert.assertTrue("AutoverConfigDecorator -> hashCode problem!", configDecorator1.hashCode() == configDecorator2.hashCode());
        Assert.assertFalse("AutoverConfigDecorator -> hashCode problem!", configDecorator1.hashCode() == configDecorator3.hashCode());
    }
}