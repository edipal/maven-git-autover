package de.palsoftware.tools.maven.git.autover.conf;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBElement;

/**
 * Simple test of the create methods.
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class ObjectFactoryTest {

    private ObjectFactory objectFactory;

    @Before
    public void setUp() {
        objectFactory = new ObjectFactory();
    }

    @Test
    public void createAutoverConfig() {
        final AutoverConfig autoverConfig = objectFactory.createAutoverConfig();
        Assert.assertNotNull("ObjectFactory -> createAutoverConfig problem", autoverConfig);
    }

    @Test
    public void createAutoverBranchConfig() {
        final AutoverBranchConfig autoverBranchConfig = objectFactory.createAutoverBranchConfig();
        Assert.assertNotNull("ObjectFactory -> createAutoverBranchConfig problem", autoverBranchConfig);
    }

    @Test
    public void createConfig() {
        final JAXBElement<AutoverConfig> autoverConfigJAXBElement = objectFactory.createConfig(new AutoverConfig());
        Assert.assertNotNull("ObjectFactory -> createConfig problem", autoverConfigJAXBElement);
    }
}