package de.palsoftware.tools.maven.git.autover;

import de.palsoftware.tools.maven.git.autover.conf.AutoverBranchConfig;
import de.palsoftware.tools.maven.git.autover.conf.StopOnEnum;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.regex.Pattern;

/**
 * {@link AutoverBranchConfig} decorator.
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class AutoverBranchConfigDecorator {

    /**
     * Fields to be excluded from equals and hashCode.
     */
    private static final String[] EXCLUDE_FIELDS = new String[]{"namePattern"};

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

    /**
     * Check if this object is equal to the object specified as a parameter.
     *
     * @param o the other object
     * @return true if the objects are equal and false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        return EqualsBuilder.reflectionEquals(this, o, EXCLUDE_FIELDS);
    }

    /**
     * Calculate the hash code.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, EXCLUDE_FIELDS);
    }
}
