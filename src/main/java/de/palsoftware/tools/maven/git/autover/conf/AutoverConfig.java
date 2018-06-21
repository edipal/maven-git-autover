
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
        "versionTagRegex",
        "includeGroupIds",
        "autoverBranchConfigs"
})
public class AutoverConfig {

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
        return Objects.equals(versionTagRegex, that.versionTagRegex)
                && Objects.equals(includeGroupIds, that.includeGroupIds)
                && Objects.equals(autoverBranchConfigs, that.autoverBranchConfigs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(versionTagRegex, includeGroupIds, autoverBranchConfigs);
    }

    @Override
    public String toString() {
        return "AutoverConfig{"
                + "versionTagRegex='" + versionTagRegex + '\''
                + ", includeGroupIds=" + includeGroupIds
                + ", autoverBranchConfigs=" + autoverBranchConfigs
                + '}';
    }
}
