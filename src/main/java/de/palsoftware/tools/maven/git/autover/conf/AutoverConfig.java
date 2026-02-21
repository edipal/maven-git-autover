
package de.palsoftware.tools.maven.git.autover.conf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class that holds the configuration for the maven extension.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "autoverConfig", namespace = "http://de.palsoftware/tools/maven/git/autover/conf", propOrder = {
        "disable",
        "versionTagRegex",
        "includeGroupIds",
        "autoverBranchConfigs"
})
public class AutoverConfig {

    /**
     * Whether the extension is disabled by default.
     * When set to true in git.autover.conf.xml, the extension is disabled (e.g. for local IDE use).
     * Can be overridden from the CLI via -Dautover.disable=false (e.g. in a CI pipeline).
     * Default is false.
     */
    @XmlElement(namespace = "http://de.palsoftware/tools/maven/git/autover/conf", required = false)
    private boolean disable;

    /**
     * The pattern for the version tag.
     * This is used to determine what tags should be considered version tags.
     */
    @XmlElement(namespace = "http://de.palsoftware/tools/maven/git/autover/conf", required = true)
    private String versionTagRegex;
    /**
     * The list of group ids.
     * Only artifacts in this group ids will be processed. When no such group is specified artifacts from all groups ids will be processed.
     */
    @XmlElement(namespace = "http://de.palsoftware/tools/maven/git/autover/conf", required = true)
    private List<String> includeGroupIds;
    /**
     * The branch configurations.
     */
    @XmlElement(namespace = "http://de.palsoftware/tools/maven/git/autover/conf", required = true)
    private List<AutoverBranchConfig> autoverBranchConfigs;

    /**
     * Constructor.
     */
    public AutoverConfig() {
        super();
    }

    /**
     * Getter.
     *
     * @return whether the extension is disabled by default (from config)
     */
    public boolean isDisable() {
        return disable;
    }

    /**
     * Setter.
     *
     * @param value whether the extension should be disabled by default (from config)
     */
    public void setDisable(final boolean value) {
        this.disable = value;
    }

    /**
     * Getter.
     *
     * @return the version tag pattern
     */
    public String getVersionTagRegex() {
        return versionTagRegex;
    }

    /**
     * Setter.
     *
     * @param value the version tag pattern
     */
    public void setVersionTagRegex(final String value) {
        this.versionTagRegex = value;
    }

    /**
     * Getter.
     *
     * @return the list of group ids
     */
    public List<String> getIncludeGroupIds() {
        if (includeGroupIds == null) {
            includeGroupIds = new ArrayList<String>();
        }
        return this.includeGroupIds;
    }

    /**
     * Getter.
     *
     * @return the list of branch configurations
     */
    public List<AutoverBranchConfig> getAutoverBranchConfigs() {
        if (autoverBranchConfigs == null) {
            autoverBranchConfigs = new ArrayList<AutoverBranchConfig>();
        }
        return this.autoverBranchConfigs;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AutoverConfig)) {
            return false;
        }
        AutoverConfig that = (AutoverConfig) o;
        return disable == that.disable
                && Objects.equals(versionTagRegex, that.versionTagRegex)
                && Objects.equals(includeGroupIds, that.includeGroupIds)
                && Objects.equals(autoverBranchConfigs, that.autoverBranchConfigs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(disable, versionTagRegex, includeGroupIds, autoverBranchConfigs);
    }

    @Override
    public String toString() {
        return "AutoverConfig{"
                + "disable=" + disable
                + ", versionTagRegex='" + versionTagRegex + '\''
                + ", includeGroupIds=" + includeGroupIds
                + ", autoverBranchConfigs=" + autoverBranchConfigs
                + '}';
    }
}
