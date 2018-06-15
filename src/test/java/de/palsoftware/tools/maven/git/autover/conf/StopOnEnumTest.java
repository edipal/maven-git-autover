package de.palsoftware.tools.maven.git.autover.conf;

import org.junit.Assert;
import org.junit.Test;

/**
 * TODO: comment
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class StopOnEnumTest {

    @Test
    public void value() {
        final StopOnEnum onFirst = StopOnEnum.ON_FIRST;
        Assert.assertTrue("StopOnEnum -> value problem!", onFirst.name().equals(onFirst.value()));
    }

    @Test
    public void fromValue() {
        final StopOnEnum onFirst = StopOnEnum.ON_FIRST;
        Assert.assertTrue("StopOnEnum -> fromValue problem!", onFirst == StopOnEnum.fromValue(onFirst.name()));
    }
}