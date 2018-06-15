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

}