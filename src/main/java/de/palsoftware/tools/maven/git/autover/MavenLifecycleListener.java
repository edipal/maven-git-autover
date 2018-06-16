package de.palsoftware.tools.maven.git.autover;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Maven lifecycle listener.
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
@Component(role = AbstractMavenLifecycleParticipant.class)
public class MavenLifecycleListener extends AbstractMavenLifecycleParticipant {

    /**
     * The holder of various information needed at different points in the maven execution..
     */
    @Requirement
    private AutoverSession autoverSession;

    /**
     * The logger.
     */
    @Requirement
    private Logger logger;

    /**
     * Entry point of the maven extension. Called before the project(s) are created.
     * Used to gather some information and read the configuration.
     *
     * @param session the maven session
     * @throws MavenExecutionException for any problems
     */
    @Override
    public void afterSessionStart(final MavenSession session) throws MavenExecutionException {
        if (logger.isDebugEnabled()) {
            logger.debug(new LocalizationHelper().getMessage(LocalizationHelper.MSG_AFTER_SESSION_START));
        }
        //the first folder up the hierarchy where an .mvn folder is found
        //if no .mvn folder is found it points to the folder where the pom file is
        final File multiModuleProjectDirectory = session.getRequest().getMultiModuleProjectDirectory();
        autoverSession.setMavenMultiModuleProjectDir(multiModuleProjectDirectory);

        //read configuration
        try {
            final ConfigReader configReader = new ConfigReader();
            configReader.setLogger(logger);
            final AutoverConfigDecorator config = configReader.readConfig(session.getSystemProperties(), multiModuleProjectDirectory);
            autoverSession.setConfig(config);
        } catch (final IOException | JAXBException | SAXException e) {
            throw new MavenExecutionException(e.getMessage(), e);
        }
    }

    /**
     * At this point all projects were read. Also the extension calculated all versions.
     * It is used to change the pom of projects for which a version was calculated. (the new pom will contain the calculated version)
     *
     * @param session the maven session
     */
    @Override
    public void afterProjectsRead(final MavenSession session) {
        if (logger.isDebugEnabled()) {
            logger.debug(new LocalizationHelper().getMessage(LocalizationHelper.MSG_AFTER_PROJECTS_READ));
        }
        //now we replace the project poms with the ones that contain the calculated version.
        final List<MavenProject> allProjects = session.getAllProjects();
        final Map<String, File> newPomFiles = autoverSession.getNewPomFiles();
        final MavenHelper mavenHelper = new MavenHelper();
        for (final MavenProject project : allProjects) {
            final Model projectModel = project.getModel();
            final String id = projectModel.getId();
            if (newPomFiles.containsKey(id)) {
                mavenHelper.replacePomFile(project, newPomFiles.get(id));
            }
        }
    }

    /**
     * Setter.
     *
     * @param anAutoverSession the session
     */
    public void setAutoverSession(final AutoverSession anAutoverSession) {
        this.autoverSession = anAutoverSession;
    }

    /**
     * Setter.
     *
     * @param value the logger
     */
    public void setLogger(final Logger value) {
        this.logger = value;
    }
}