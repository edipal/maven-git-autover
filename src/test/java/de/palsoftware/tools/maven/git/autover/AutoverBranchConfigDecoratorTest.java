package de.palsoftware.tools.maven.git.autover;

import de.palsoftware.tools.maven.git.autover.conf.AutoverBranchConfig;
import de.palsoftware.tools.maven.git.autover.conf.StopOnEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests getters and setters for {@link AutoverBranchConfigDecorator}.
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class AutoverBranchConfigDecoratorTest {

    private AutoverBranchConfig branchConfig;

    @Before
    public void setUp() {
        branchConfig = new AutoverBranchConfig();
        branchConfig.setNameRegex("AABBCC");
    }

    @Test
    public void nameRegex() {
        String nameRegex = "AABBCC";
        branchConfig.setNameRegex(nameRegex);

        final AutoverBranchConfigDecorator branchConfigDecorator = new AutoverBranchConfigDecorator(branchConfig);
        Assert.assertTrue("AutoverBranchConfigDecorator -> NameRegex setter / getter problem!", nameRegex.equalsIgnoreCase(branchConfigDecorator.getNameRegex()));

        nameRegex = "112233";
        branchConfig.setNameRegex(nameRegex);
        Assert.assertTrue("AutoverBranchConfigDecorator -> NameRegex setter / getter problem!", nameRegex.equalsIgnoreCase(branchConfigDecorator.getNameRegex()));
    }

    @Test
    public void namePattern() {
        String namePattern = "AABBCC";
        branchConfig.setNameRegex(namePattern);
        final AutoverBranchConfigDecorator branchConfigDecorator = new AutoverBranchConfigDecorator(branchConfig);
        Assert.assertTrue("AutoverBranchConfigDecorator -> NamePattern setter / getter problem!", namePattern.equalsIgnoreCase(branchConfigDecorator.getNamePattern().pattern()));
    }

    @Test
    public void stopOn() {
        StopOnEnum stopOnEnum = StopOnEnum.ON_FIRST;
        branchConfig.setStopOn(stopOnEnum);
        final AutoverBranchConfigDecorator branchConfigDecorator = new AutoverBranchConfigDecorator(branchConfig);
        Assert.assertTrue("AutoverBranchConfigDecorator -> StopOn setter / getter problem!", stopOnEnum == branchConfigDecorator.getStopOn());

        stopOnEnum = StopOnEnum.ON_FIRST_ANN;
        branchConfig.setStopOn(stopOnEnum);
        Assert.assertTrue("AutoverBranchConfigDecorator -> StopOn setter / getter problem!", stopOnEnum == branchConfigDecorator.getStopOn());
    }

    @Test
    public void toStringTest() {
        final AutoverBranchConfig config1 = new AutoverBranchConfig();
        config1.setNameRegex("AABBCC");
        config1.setStopOn(StopOnEnum.ON_FIRST);
        final AutoverBranchConfigDecorator config1Decorator = new AutoverBranchConfigDecorator(config1);
        final String toString1 = config1Decorator.toString();
        final AutoverBranchConfig config2 = new AutoverBranchConfig();
        config2.setNameRegex("ABCDEF");
        final AutoverBranchConfigDecorator config2Decorator = new AutoverBranchConfigDecorator(config2);
        final String toString2 = config2Decorator.toString();

        Assert.assertFalse("AutoverBranchConfigDecorator ->  toString problem!", toString1.equals(toString2));
    }

    @Test
    public void equalsAndHashCode() {
        final AutoverBranchConfig config1 = new AutoverBranchConfig();
        config1.setNameRegex("AABBCC");
        config1.setStopOn(StopOnEnum.ON_FIRST);
        final AutoverBranchConfigDecorator configDecorator1 = new AutoverBranchConfigDecorator(config1);
        final AutoverBranchConfigDecorator configDecorator2 = new AutoverBranchConfigDecorator(config1);
        final AutoverBranchConfig config2 = new AutoverBranchConfig();
        config2.setNameRegex("AABBCC");
        config2.setStopOn(StopOnEnum.ON_FIRST);
        final AutoverBranchConfigDecorator configDecorator3 = new AutoverBranchConfigDecorator(config2);
        final AutoverBranchConfig config3 = new AutoverBranchConfig();
        config3.setNameRegex("112233");
        config3.setStopOn(StopOnEnum.ON_FIRST);
        final AutoverBranchConfigDecorator configDecorator4 = new AutoverBranchConfigDecorator(config3);

        //equals
        Assert.assertTrue("AutoverBranchConfigDecorator -> equals problem!", configDecorator1.equals(configDecorator1));
        Assert.assertTrue("AutoverBranchConfigDecorator -> equals problem!", configDecorator1.equals(configDecorator2));
        Assert.assertFalse("AutoverBranchConfigDecorator -> equals problem!", configDecorator1.equals(null));
        Assert.assertFalse("AutoverBranchConfigDecorator -> equals problem!", configDecorator1.equals(new Object()));
        Assert.assertTrue("AutoverBranchConfigDecorator -> equals problem!", configDecorator1.equals(configDecorator3));
        Assert.assertFalse("AutoverBranchConfigDecorator -> equals problem!", configDecorator1.equals(configDecorator4));

        //hashCode
        Assert.assertTrue("AutoverBranchConfigDecorator -> hashCode equals problem!", configDecorator1.hashCode() == configDecorator1.hashCode());
        Assert.assertTrue("AutoverBranchConfigDecorator -> hashCode equals problem!", configDecorator1.hashCode() == configDecorator2.hashCode());
        Assert.assertTrue("AutoverBranchConfigDecorator -> hashCode equals problem!", configDecorator1.hashCode() == configDecorator3.hashCode());
        Assert.assertFalse("AutoverBranchConfigDecorator -> hashCode equals problem!", configDecorator1.hashCode() == configDecorator4.hashCode());
    }
}