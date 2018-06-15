package de.palsoftware.tools.maven.git.autover;

import org.codehaus.plexus.component.annotations.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class that holds information needed at different points in the maven execution.
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
@Component(role = AutoverSession.class, instantiationStrategy = "singleton")
public class AutoverSession {
    /**
     * The map of new pom files - with the calculated version in it.
     * the key is the ID of the artifact and the value is the new pom file
     */
    private final Map<String, File> newPomFiles = new HashMap<>();
    /**
     * The multi module project dir.
     * (the first dir up the path tree that contains the a .mvn folder or the folder where the pom file is)
     */
    private File mavenMultiModuleProjectDir;
    /**
     * The configuration of the maven extension.
     */
    private AutoverConfigDecorator config;

    /**
     * Getter.
     *
     * @return the multi module project dir
     */
    public File getMavenMultiModuleProjectDir() {
        return mavenMultiModuleProjectDir;
    }

    /**
     * Setter.
     *
     * @param value the multi module project dir
     */
    public void setMavenMultiModuleProjectDir(final File value) {
        this.mavenMultiModuleProjectDir = value;
    }

    /**
     * Getter.
     *
     * @return the configuration of the maven extension
     */
    public AutoverConfigDecorator getConfig() {
        return config;
    }

    /**
     * Setter.
     *
     * @param value the configuration of the maven extension
     */
    public void setConfig(final AutoverConfigDecorator value) {
        this.config = value;
    }

    /**
     * Getter.
     *
     * @return the map of new pom files
     */
    public Map<String, File> getNewPomFiles() {
        return newPomFiles;
    }

    /**
     * Check if this object is equal to the object specified as a parameter.
     *
     * @param o the other object
     * @return true if the objects are equal and false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AutoverSession)) {
            return false;
        }
        AutoverSession that = (AutoverSession) o;
        return Objects.equals(getNewPomFiles(), that.getNewPomFiles())
                && Objects.equals(getMavenMultiModuleProjectDir(), that.getMavenMultiModuleProjectDir())
                && Objects.equals(getConfig(), that.getConfig());
    }

    /**
     * Calculate the hash code.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(getNewPomFiles(), getMavenMultiModuleProjectDir(), getConfig());
    }
}
