package de.palsoftware.tools.maven.git.autover;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Helper for localization.
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class LocalizationHelper {

    /**
     * Prefix for log messages.
     */
    public static final String MESSAGE_PREFIX = "[maven-git-autover] ";

    /**
     * Resource bundle Key for the invalid key error message.
     * 1 parameter: the invalid key
     */
    public static final String ERR_INVALID_RB_KEY = "err.invalid.rb.key";
    /**
     * Resource bundle key for the empty version patch message.
     * 1 parameter: the version tag pattern
     */
    public static final String ERR_NO_GROUP_DEFINED_FOR_VERSION_PATCH = "err.no.group.defined.for.version.patch";
    /**
     * Resource bundle key for the empty version patch message.
     * 1 parameter: the version tag pattern
     */
    public static final String ERR_EMPTY_VERSION_PATCH_DETECTED = "err.empty.version.patch.detected";
    /**
     * Resource bundle key for the invalid version patch message.
     * 1 parameter: the invalid version patch
     * 2 parameter: the version tag pattern
     */
    public static final String ERR_INVALID_VERSION_PATCH_DETECTED = "err.invalid.version.patch.detected";
    /**
     * Resource bundle key for the invalid configuration file.
     * 1 parameter: the specified configuration file path
     */
    public static final String ERR_CONF_FILE_INVALID = "err.conf.file.invalid";
    /**
     * Resource bundle key for problems reading the configuration file.
     * 1 parameter: the error message
     */
    public static final String ERR_CONF_FILE_CANT_READ = "err.conf.file.cant.read";
    /**
     * Resource bundle key for err message in case not tag is found.
     */
    public static final String ERR_NO_TAG_FOUND = "err.no.tag.found";
    /**
     * Resource bundle key for the configuration file that will be used.
     * 1 parameter: the configuration that will be used
     */
    public static final String MSG_CONF_FILE_USER = "msg.conf.file.user";
    /**
     * Resource bundle key for the message that the proj will be processed.
     * 1 parameter: the project/model id
     */
    public static final String MSG_PROJ_WILL_BE_PROCESSED = "msg.proj.will.be.processed";
    /**
     * Resource bundle key for the message that the proj will not be processed.
     * 1 parameter: the project/model id
     */
    public static final String MSG_PROJ_WILL_NOT_BE_PROCESSED = "msg.proj.will.not.be.processed";
    /**
     * Resource bundle key for the message that the after session start was called.
     */
    public static final String MSG_AFTER_SESSION_START = "msg.after.session.start";
    /**
     * Resource bundle key for the message that the after projects read was called.
     */
    public static final String MSG_AFTER_PROJECTS_READ = "msg.after.projects.read";

    /**
     * Resource bundle key for the message that provides the calculated version.
     * 1. the calculated version
     */
    public static final String MSG_CALCULATED_VERSION = "msg.calculated.version";
    /**
     * Resource bundle key for the message that the on event was called.
     */
    public static final String MSG_ON_EVENT_CALLED = "msg.on.event.called";
    /**
     * Resource bundle key for the message that the model processor was called.
     */
    public static final String MSG_MODEL_PROCESSOR_CALLED = "msg.model.processor.called";
    /**
     * Resource bundle key for the message that the extension is disabled.
     */
    public static final String MSG_DISABLED = "msg.disabled";
    /**
     * Resource bundle key for the message that the changing of the pom is disabled.
     */
    public static final String MSG_POM_CHANGE_DISABLED = "msg.pom.change.disabled";
    /**
     * The resource bundle.
     */
    private static final ResourceBundle RB = ResourceBundle.getBundle("de.palsoftware.tools.maven.git.autover.MavenGitAutover", Locale.getDefault());

    /**
     * Constructor.
     */
    public LocalizationHelper() {
        super();
    }

    /**
     * Get he localized message.
     *
     * @param messageKey the key
     * @param params     the message parameters (optional)
     * @return the message
     */
    public String getMessage(final String messageKey, final Object... params) {
        if (RB.containsKey(messageKey)) {
            final String messagePattern = RB.getString(messageKey);
            return MESSAGE_PREFIX + " " + MessageFormat.format(messagePattern, params);
        } else {
            final String invalidKeyMessage = getMessage(ERR_INVALID_RB_KEY, messageKey);
            throw new IllegalArgumentException(invalidKeyMessage);
        }
    }
}
