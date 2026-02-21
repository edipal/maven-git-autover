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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Maven lifecycle listener.
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
@Component(role = AbstractMavenLifecycleParticipant.class)
public class MavenLifecycleListener extends AbstractMavenLifecycleParticipant {
    /**
     * Default constructor for MavenLifecycleListener.
     */
    public MavenLifecycleListener() {
        // ...existing code...
    }

    /**
     * Property key for disabling the extension.
     */
    public static final String DISABLE_PROPERTY_KEY = "autover.disable";
    /**
     * Property key for disabling pom changes.
     */
    public static final String DISABLE_POM_CHANGE_PROPERTY_KEY = "autover.disable.pom.change";
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
        final Properties systemProperties = session.getSystemProperties();

        final String disableCliStr = systemProperties.getProperty(DISABLE_PROPERTY_KEY);

        // If explicitly disabled via CLI, skip config reading entirely.
        if (Boolean.parseBoolean(disableCliStr)) {
            autoverSession.setDisable(true);
            if (logger.isDebugEnabled()) {
                logger.debug(new LocalizationHelper().getMessage(LocalizationHelper.MSG_DISABLED));
            }
            return;
        }

        // Read config once - needed whether CLI was not specified or CLI said false.
        try {
            final File multiModuleProjectDirectory = session.getRequest().getMultiModuleProjectDirectory();
            autoverSession.setMavenMultiModuleProjectDir(multiModuleProjectDirectory.getCanonicalFile());
        } catch (final IOException e) {
            throw new MavenExecutionException(e.getMessage(), e);
        }
        try {
            final ConfigReader configReader = new ConfigReader();
            configReader.setLogger(logger);
            final AutoverConfigDecorator config = configReader.readConfig(systemProperties, autoverSession.getMavenMultiModuleProjectDir());
            autoverSession.setConfig(config);
        } catch (final IOException | JAXBException | SAXException e) {
            throw new MavenExecutionException(e.getMessage(), e);
        }

        // If CLI didn't specify disable, use the config value; otherwise CLI already said false.
        if (disableCliStr == null) {
            autoverSession.setDisable(autoverSession.getConfig().isDisable());
        } else {
            autoverSession.setDisable(false); // true is handled above, so if CLI specified disable, it must be false here.
        }

        if (autoverSession.isDisable()) {
            if (logger.isDebugEnabled()) {
                logger.debug(new LocalizationHelper().getMessage(LocalizationHelper.MSG_DISABLED));
            }
            return;
        }

        //check if the changing of the pom should be disabled
        final String disablePomChangeStr = systemProperties.getProperty(DISABLE_POM_CHANGE_PROPERTY_KEY);
        final boolean disablePomChange = Boolean.parseBoolean(disablePomChangeStr);
        autoverSession.setDisablePomChange(disablePomChange);

        if (disablePomChange) {
            if (logger.isDebugEnabled()) {
                logger.debug(new LocalizationHelper().getMessage(LocalizationHelper.MSG_POM_CHANGE_DISABLED));
            }
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
        //only replace pom if extension is not disabled or the pom changing is not disabled
        if (!autoverSession.isDisable() && !autoverSession.isDisablePomChange()) {
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
    }

    @Override
    public void afterSessionEnd(final MavenSession session) {
        if (logger.isDebugEnabled()) {
            logger.debug(new LocalizationHelper().getMessage(LocalizationHelper.MSG_AFTER_SESSION_END));
        }
        if (!autoverSession.isDisable() && !autoverSession.isDisablePomChange()) {
            //delete generated poms
            final Collection<File> newPomFiles = autoverSession.getNewPomFiles().values();
            for (final File newPomFile : newPomFiles) {
                final boolean deleteOk = newPomFile.delete();
                if (!deleteOk) {
                    logger.warn(new LocalizationHelper().getMessage(LocalizationHelper.ERR_CAN_NOT_DELETE_POM_FILE, newPomFile.getAbsolutePath()));
                }
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
