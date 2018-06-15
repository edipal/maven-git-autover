package de.palsoftware.tools.maven.git.autover;

import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO: comment
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class MavenEventSpyTest {

    private MavenEventSpy mavenEventSpy;

    @Before
    public void setUp() {
        mavenEventSpy = new MavenEventSpy();
        final AutoverSession autoverSession = new AutoverSession();
        mavenEventSpy.setAutoverSession(autoverSession);
        mavenEventSpy.setLogger(new ConsoleLogger());
    }

    @Test
    public void onEvent() throws Exception {
        mavenEventSpy.onEvent(new Object());
    }
}