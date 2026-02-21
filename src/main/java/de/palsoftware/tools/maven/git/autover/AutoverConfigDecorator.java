package de.palsoftware.tools.maven.git.autover;

import de.palsoftware.tools.maven.git.autover.conf.AutoverBranchConfig;
import de.palsoftware.tools.maven.git.autover.conf.AutoverConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
     * @return whether the extension is disabled by default (from config).
     *         Can be overridden by the CLI property -Dautover.disable.
     */
    public boolean isDisable() {
        return autoverConfig.isDisable();
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AutoverConfigDecorator)) {
            return false;
        }
        AutoverConfigDecorator that = (AutoverConfigDecorator) o;
        return Objects.equals(autoverConfig, that.autoverConfig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(autoverConfig);
    }

    @Override
    public String toString() {
        return "AutoverConfigDecorator{"
                + "autoverConfig=" + autoverConfig
                + '}';
    }
}
