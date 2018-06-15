package de.palsoftware.tools.maven.git.autover;

import de.palsoftware.tools.maven.git.autover.conf.AutoverBranchConfig;
import de.palsoftware.tools.maven.git.autover.conf.AutoverConfig;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * {@link AutoverConfig} decorator..
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class AutoverConfigDecorator {

    /**
     * Fields to be excluded from equals and hashCode.
     */
    private static final String[] EXCLUDE_FIELDS = new String[]{"versionTagPattern", "autoverBranchConfigs"};

    /**
     * the configuration.
     */
    private final AutoverConfig autoverConfig;

    /**
     * The precompiled pattern for the version regex.
     */
    private final Pattern versionTagPattern;
    /**
     * the branch configurations.
     */
    private final List<AutoverBranchConfigDecorator> autoverBranchConfigs;

    /**
     * Constructor.
     *
     * @param theAutoverConfig the configuration to use
     */
    public AutoverConfigDecorator(final AutoverConfig theAutoverConfig) {
        this.autoverConfig = theAutoverConfig;
        this.versionTagPattern = Pattern.compile(theAutoverConfig.getVersionTagRegex());
        final List<AutoverBranchConfig> originalAutoverBranchConfigs = theAutoverConfig.getAutoverBranchConfigs();
        autoverBranchConfigs = new ArrayList<>(originalAutoverBranchConfigs.size());
        for (final AutoverBranchConfig originalAutoverBranchConfig : originalAutoverBranchConfigs) {
            final AutoverBranchConfigDecorator autoverBranchConfigDecorator = new AutoverBranchConfigDecorator(originalAutoverBranchConfig);
            autoverBranchConfigs.add(autoverBranchConfigDecorator);
        }
    }

    /**
     * Getter.
     *
     * @return the version tag regex
     */
    public String getVersionTagRegex() {
        return autoverConfig.getVersionTagRegex();
    }

    /**
     * Getter.
     *
     * @return the version tag regex pattern
     */
    public Pattern getVersionTagPattern() {
        return versionTagPattern;
    }

    /**
     * Getter.
     *
     * @return the list of included group ids
     */
    public List<String> getIncludeGroupIds() {
        return autoverConfig.getIncludeGroupIds();
    }

    /**
     * Getter.
     *
     * @return the list of branch configurations
     */
    public List<AutoverBranchConfigDecorator> getAutoverBranchConfigs() {
        return autoverBranchConfigs;
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
