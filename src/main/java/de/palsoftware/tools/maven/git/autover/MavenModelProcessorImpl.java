package de.palsoftware.tools.maven.git.autover;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.DefaultModelProcessor;
import org.apache.maven.model.building.ModelProcessor;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

/**
 * Custom implementation for the {@link ModelProcessor} to calculate the version.
 * DefaultProjectBuilder -&gt; DefaultModelBuilder -&gt; DefaultModelProcessor.
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
@Component(role = ModelProcessor.class)
public class MavenModelProcessorImpl extends DefaultModelProcessor {

    /**
     * The logger.
     */
    @Requirement
    private Logger logger;
    /**
     * The session.
     */
    @Requirement
    private AutoverSession autoverSession;

    /**
     * Call the default implementation to read te model and calculate and replace the version after.
     *
     * @param input   the input
     * @param options the options
     * @return the Model
     * @throws IOException for io problems
     */
    @Override
    public Model read(final File input, final Map<String, ?> options) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug(new LocalizationHelper().getMessage(LocalizationHelper.MSG_MODEL_PROCESSOR_CALLED));
        }
        final Model model = super.read(input, options);
        setAutoVersion(model, options);
        return model;
    }

    /**
     * Call the default implementation to read te model and calculate and replace the version after.
     *
     * @param input   the input
     * @param options the options
     * @return the Model
     * @throws IOException for io problems
     */
    @Override
    public Model read(final Reader input, final Map<String, ?> options) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug(new LocalizationHelper().getMessage(LocalizationHelper.MSG_MODEL_PROCESSOR_CALLED));
        }
        final Model model = super.read(input, options);
        setAutoVersion(model, options);
        return model;
    }

    /**
     * Call the default implementation to read te model and calculate and replace the version after.
     *
     * @param input   the input
     * @param options the options
     * @return the Model
     * @throws IOException for io problems
     */
    @Override
    public Model read(final InputStream input, final Map<String, ?> options) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug(new LocalizationHelper().getMessage(LocalizationHelper.MSG_MODEL_PROCESSOR_CALLED));
        }
        final Model model = super.read(input, options);
        setAutoVersion(model, options);
        return model;
    }

    /**
     * Call the maven helper to set the autoversion.
     *
     * @param model   the model
     * @param options the options
     * @throws IOException for io problems
     */
    private void setAutoVersion(final Model model, final Map<String, ?> options) throws IOException {
        final MavenHelper mh = new MavenHelper();
        mh.setLogger(logger);
        mh.setAutoVersion(model, options, autoverSession);
    }

    /**
     * Setter.
     *
     * @param value the logger
     */
    public void setLogger(final Logger value) {
        this.logger = value;
    }

    /**
     * Setter.
     *
     * @param value the autover session
     */
    public void setAutoverSession(final AutoverSession value) {
        this.autoverSession = value;
    }


}
