package de.palsoftware.tools.maven.git.autover;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.eventspy.EventSpy;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

/**
 * Event spy. Now it doesn't do anything.
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
@Component(role = EventSpy.class)
public class MavenEventSpy extends AbstractEventSpy {

    /**
     * The holder of various information needed at different points in the maven execution..
     */
    @Requirement
    private AutoverSession autoverSession;

    /**
     * The logger.
     */
    @Requirement
    private Logger logger;

    /**
     * Listener on events.
     *
     * @param event the event (should be of type {@link org.apache.maven.execution.ExecutionEvent}
     * @throws Exception for problems
     */
    @Override
    public void onEvent(final Object event) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(new LocalizationHelper().getMessage(LocalizationHelper.MSG_ON_EVENT_CALLED));
        }
        super.onEvent(event);
    }

    /**
     * Setter.
     *
     * @param theAutoverSession the autover session
     */
    public void setAutoverSession(final AutoverSession theAutoverSession) {
        this.autoverSession = theAutoverSession;
    }

    /**
     * Setter.
     *
     * @param value the logger
     */
    public void setLogger(final Logger value) {
        this.logger = value;
    }
}
