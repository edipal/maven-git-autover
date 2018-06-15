package de.palsoftware.tools.maven.git.autover;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelProcessor;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Helper for maven operations.
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class MavenHelper {

    /**
     * The prefix for the generated pom file.
     */
    public static final String MVN_GIT_AUTOVER_POM_PREFIX = "mvn_git_autover_pom.";

    /**
     * The logger.
     */
    private Logger logger;

    /**
     * Constructor.
     */
    public MavenHelper() {
        super();
    }

    /**
     * Replace the pom of the specified project.
     * Should be done before the project execution.
     *
     * @param project    the maven project
     * @param newPomFile the new pom file (with the actual version in it)
     */
    public void replacePomFile(final MavenProject project, final File newPomFile) {
        project.setPomFile(newPomFile);
    }

    /**
     * Calculate and set the version.
     *
     * @param model          the model
     * @param options        the options
     * @param autoverSession the autover session
     * @throws IOException for IO problems
     */
    public void setAutoVersion(final Model model, final Map<String, ?> options, final AutoverSession autoverSession) throws IOException {
        final Parent parent = model.getParent();
        //groupId can be inherited
        String groupId = model.getGroupId();
        if ((groupId == null) && (parent != null)) {
            groupId = parent.getGroupId();
        }
        //version can be inherited
        final String versionId = model.getVersion();
        final ConfigHelper configHelper = new ConfigHelper(autoverSession.getConfig());
        if (versionId != null) {
            final Object modelSource = options.get(ModelProcessor.SOURCE);
            if (modelSource instanceof FileModelSource) {
                final boolean processArtifact = configHelper.shouldProcessArtifact(model.getVersion(), groupId, model.getArtifactId());
                if (processArtifact) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(new LocalizationHelper().getMessage(LocalizationHelper.MSG_PROJ_WILL_BE_PROCESSED, model.getId()));
                    }
                    final File pomParentFolder = ((FileModelSource) modelSource).getFile().getParentFile();
                    final String calculatedVer;
                    final GitHelper gitHelper = new GitHelper(pomParentFolder, configHelper);
                    final GitAnalysisResult gitAnalysisResult = gitHelper.analyze();
                    if (gitAnalysisResult.getTagName() == null) {
                        throw new RuntimeException(new LocalizationHelper().getMessage(LocalizationHelper.ERR_NO_TAG_FOUND));
                    }
                    calculatedVer = configHelper.calculateVer(gitAnalysisResult);
                    if (calculatedVer != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug(new LocalizationHelper().getMessage(LocalizationHelper.MSG_CALCULATED_VERSION, calculatedVer));
                        }
                        model.setVersion(calculatedVer);
                        final String modelId = model.getId();
                        final File newPomFile = writeNewPomFile(model, pomParentFolder);
                        final Map<String, File> newPomFiles = autoverSession.getNewPomFiles();
                        newPomFiles.put(modelId, newPomFile);
                    }
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug(new LocalizationHelper().getMessage(LocalizationHelper.MSG_PROJ_WILL_NOT_BE_PROCESSED, model.getId()));
                    }
                }
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(new LocalizationHelper().getMessage(LocalizationHelper.MSG_PROJ_WILL_NOT_BE_PROCESSED, model.getId()));
            }
        }
    }

    /**
     * Write the new pom file (with the version in it).
     *
     * @param model           the model (the version should be calculated by now)
     * @param pomParentFolder the parent folder of the original pom file
     * @return the new pom file
     * @throws IOException for IO problems
     */
    public File writeNewPomFile(final Model model, final File pomParentFolder) throws IOException {
        final long currentTimeMillis = System.currentTimeMillis();
        final String newPomFileName = MVN_GIT_AUTOVER_POM_PREFIX + String.valueOf(currentTimeMillis) + ".xml";
        final File newPomFile = new File(pomParentFolder, newPomFileName);
        newPomFile.deleteOnExit();
        try (FileWriter fileWriter = new FileWriter(newPomFile)) {
            new MavenXpp3Writer().write(fileWriter, model);
        }
        return newPomFile;
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
