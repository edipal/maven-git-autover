
package de.palsoftware.tools.maven.git.autover.conf;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * JAXB object factory.
 */
@XmlRegistry
public class ObjectFactory {

    /**
     * Config name.
     */
    private static final QName CONFIG_QNAME = new QName("http://de.palsoftware/tools/maven/git/autover/conf", "config");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.palsoftware.tools.maven.git.autover.conf.
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AutoverConfig }.
     *
     * @return the configuration object
     */
    public AutoverConfig createAutoverConfig() {
        return new AutoverConfig();
    }

    /**
     * Create an instance of {@link AutoverBranchConfig }.
     *
     * @return the branch configuration object
     */
    public AutoverBranchConfig createAutoverBranchConfig() {
        return new AutoverBranchConfig();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AutoverConfig }{@code >}}.
     *
     * @param value the config object
     * @return the jaxb element
     */
    @XmlElementDecl(namespace = "http://de.palsoftware/tools/maven/git/autover/conf", name = "config")
    public JAXBElement<AutoverConfig> createConfig(final AutoverConfig value) {
        return new JAXBElement<AutoverConfig>(CONFIG_QNAME, AutoverConfig.class, null, value);
    }

}
