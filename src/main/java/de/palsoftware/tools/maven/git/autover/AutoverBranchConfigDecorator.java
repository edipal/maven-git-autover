package de.palsoftware.tools.maven.git.autover;

import de.palsoftware.tools.maven.git.autover.conf.AutoverBranchConfig;
import de.palsoftware.tools.maven.git.autover.conf.StopOnEnum;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * {@link AutoverBranchConfig} decorator.
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class AutoverBranchConfigDecorator {

    /**
     * the configuration.
     */
    private final AutoverBranchConfig autoverBranchConfig;

    /**
     * the precompiled name pattern.
     */
    private final Pattern namePattern;

    /**
     * Constructor.
     *
     * @param theAutoverBranchConfig the configuration to use
     */
    public AutoverBranchConfigDecorator(final AutoverBranchConfig theAutoverBranchConfig) {
        this.autoverBranchConfig = theAutoverBranchConfig;
        namePattern = Pattern.compile(theAutoverBranchConfig.getNameRegex());
    }

    /**
     * Getter.
     *
     * @return the name regex
     */
    public String getNameRegex() {
        return autoverBranchConfig.getNameRegex();
    }

    /**
     * Getter.
     *
     * @return the branch name pattern
     */
    public Pattern getNamePattern() {
        return namePattern;
    }

    /**
     * Getter.
     *
     * @return the stop on value setting
     */
    public StopOnEnum getStopOn() {
        return autoverBranchConfig.getStopOn();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AutoverBranchConfigDecorator)) {
            return false;
        }
        AutoverBranchConfigDecorator that = (AutoverBranchConfigDecorator) o;
        return Objects.equals(autoverBranchConfig, that.autoverBranchConfig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(autoverBranchConfig);
    }

    @Override
    public String toString() {
        return "AutoverBranchConfigDecorator{"
                + "autoverBranchConfig=" + autoverBranchConfig
                + '}';
    }
}
