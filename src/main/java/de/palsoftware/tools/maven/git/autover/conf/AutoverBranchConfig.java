
package de.palsoftware.tools.maven.git.autover.conf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Class that holds the configuration for a git branch.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "autoverBranchConfig", namespace = "http://de.palsoftware/tools/maven/git/autover/conf", propOrder = {
        "nameRegex",
        "stopOn"
})
public class AutoverBranchConfig {

    /**
     * The name pattern for the branch.
     * This is used to determine the branches that should use this configuration.
     */
    @XmlElement(namespace = "http://de.palsoftware/tools/maven/git/autover/conf", required = true)
    private String nameRegex;
    /**
     * The setting for stop on.
     * The stop on setting to be used by branches that match the above pattern.
     */
    @XmlElement(namespace = "http://de.palsoftware/tools/maven/git/autover/conf", required = true)
    @XmlSchemaType(name = "string")
    private StopOnEnum stopOn;

    /**
     * Constructor.
     */
    public AutoverBranchConfig() {
        super();
    }

    /**
     * Getter.
     *
     * @return the name pattern
     */
    public String getNameRegex() {
        return nameRegex;
    }

    /**
     * Setter.
     *
     * @param value the name pattern
     */
    public void setNameRegex(final String value) {
        this.nameRegex = value;
    }

    /**
     * Getter.
     *
     * @return the stop on setting
     */
    public StopOnEnum getStopOn() {
        return stopOn;
    }

    /**
     * Setter.
     *
     * @param value the stop on setting
     */
    public void setStopOn(final StopOnEnum value) {
        this.stopOn = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AutoverBranchConfig)) {
            return false;
        }
        AutoverBranchConfig that = (AutoverBranchConfig) o;
        return Objects.equals(nameRegex, that.nameRegex)
                && stopOn == that.stopOn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameRegex, stopOn);
    }

    @Override
    public String toString() {
        return "AutoverBranchConfig{"
                + "nameRegex='" + nameRegex + '\''
                + ", stopOn=" + stopOn
                + '}';
    }
}
