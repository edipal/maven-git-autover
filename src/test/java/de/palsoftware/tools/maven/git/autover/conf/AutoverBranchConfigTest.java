package de.palsoftware.tools.maven.git.autover.conf;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests getters and setters for {@link AutoverBranchConfig}.
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class AutoverBranchConfigTest {

    private AutoverBranchConfig config;

    @Before
    public void setUp() {
        config = new AutoverBranchConfig();
    }

    @Test
    public void nameRegex() {
        String nameRegex = "AABBCC";
        config.setNameRegex(nameRegex);
        Assert.assertTrue("AutoverBranchConfig -> NameRegex setter / getter problem!", nameRegex.equalsIgnoreCase(config.getNameRegex()));

        nameRegex = "112233";
        config.setNameRegex(nameRegex);
        Assert.assertTrue("AutoverBranchConfig -> NameRegex setter / getter problem!", nameRegex.equalsIgnoreCase(config.getNameRegex()));
    }

    @Test
    public void stopOn() {
        StopOnEnum stopOnEnum = StopOnEnum.ON_FIRST;
        config.setStopOn(stopOnEnum);
        Assert.assertTrue("AutoverBranchConfig -> StopOn setter / getter problem!", stopOnEnum == config.getStopOn());

        stopOnEnum = StopOnEnum.ON_FIRST_ANN;
        config.setStopOn(stopOnEnum);
        Assert.assertTrue("AutoverBranchConfig -> StopOn setter / getter problem!", stopOnEnum == config.getStopOn());
    }

    @Test
    public void toStringTest() {
        config.setNameRegex("AABBCC");
        config.setStopOn(StopOnEnum.ON_FIRST);
        final String toString1 = config.toString();
        config.setNameRegex("ABCDEF");
        final String toString2 = config.toString();

        Assert.assertFalse("AutoverBranchConfig ->  toString problem!", toString1.equals(toString2));
    }

    @Test
    public void equalsAndHashCode() {
        final AutoverBranchConfig config1 = new AutoverBranchConfig();
        config1.setNameRegex("AABBCC");
        config1.setStopOn(StopOnEnum.ON_FIRST);
        final AutoverBranchConfig config2 = new AutoverBranchConfig();
        config2.setNameRegex("AABBCC");
        config2.setStopOn(StopOnEnum.ON_FIRST);
        final AutoverBranchConfig config3 = new AutoverBranchConfig();
        config3.setNameRegex("112233");
        config3.setStopOn(StopOnEnum.ON_FIRST);

        //equals
        Assert.assertTrue("AutoverBranchConfig -> equals problem!", config1.equals(config1));
        Assert.assertTrue("AutoverBranchConfig -> equals problem!", config1.equals(config2));
        Assert.assertFalse("AutoverBranchConfig -> equals problem!", config1.equals(null));
        Assert.assertFalse("AutoverBranchConfig -> equals problem!", config1.equals(new Object()));
        Assert.assertFalse("AutoverBranchConfig -> equals problem!", config1.equals(config3));

        //hashCode
        Assert.assertTrue("AutoverBranchConfig -> hashCode equals problem!", config1.hashCode() == config1.hashCode());
        Assert.assertTrue("AutoverBranchConfig -> hashCode equals problem!", config1.hashCode() == config2.hashCode());
        Assert.assertFalse("AutoverBranchConfig -> hashCode equals problem!", config1.hashCode() == config3.hashCode());
    }
}