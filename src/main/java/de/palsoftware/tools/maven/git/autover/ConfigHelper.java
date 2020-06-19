package de.palsoftware.tools.maven.git.autover;

import de.palsoftware.tools.maven.git.autover.conf.AutoverBranchConfig;
import de.palsoftware.tools.maven.git.autover.conf.AutoverConfig;
import de.palsoftware.tools.maven.git.autover.conf.StopOnEnum;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to apply the maven extension configuration.
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class ConfigHelper {

    /**
     * The default version tag regular expression.
     */
    public static final String DEFAULT_VERSION_TAG_REGEX = "[0-9]+\\.[0-9]+\\.([0-9]+)(-SNAPSHOT)?";
    /**
     * Default configuration for the master branch.
     */
    public static final AutoverBranchConfig DEFAULT_MASTER_CONFIG;
    /**
     * Default configuration for the release branch.
     */
    public static final AutoverBranchConfig DEFAULT_RELEASE_CONFIG;
    /**
     * Default configuration for the feature branch.
     */
    public static final AutoverBranchConfig DEFAULT_FEATURE_CONFIG;
    /**
     * Default configuration for the bugfix branch.
     */
    public static final AutoverBranchConfig DEFAULT_BUGFIX_CONFIG;
    /**
     * Default configuration for the PR branch.
     */
    public static final AutoverBranchConfig DEFAULT_PR_CONFIG;
    /**
     * Default configuration for the other branches.
     */
    public static final AutoverBranchConfig DEFAULT_OTHER_CONFIG;
    /**
     * The marker artifact version that will indicate that the maven extension should calculate the version.
     */
    public static final String TO_BE_CALCULATED_ARTIFACT_VERSION = "0.0.0-SNAPSHOT";
    /**
     * The SNAPSHOT suffix for a version.
     */
    private static final String SNAPSHOT_VERSION_SUFFIX = "-SNAPSHOT";

    static {
        DEFAULT_MASTER_CONFIG = new AutoverBranchConfig();
        DEFAULT_MASTER_CONFIG.setNameRegex("master");
        DEFAULT_MASTER_CONFIG.setStopOn(StopOnEnum.ON_FIRST_LIGHT);
        DEFAULT_RELEASE_CONFIG = new AutoverBranchConfig();
        DEFAULT_RELEASE_CONFIG.setNameRegex("release/.*");
        DEFAULT_RELEASE_CONFIG.setStopOn(StopOnEnum.ON_FIRST_ANN);
        DEFAULT_FEATURE_CONFIG = new AutoverBranchConfig();
        DEFAULT_FEATURE_CONFIG.setNameRegex("feature/([A-Z0-9]+-[0-9]+)-.*");
        DEFAULT_FEATURE_CONFIG.setStopOn(StopOnEnum.ON_FIRST_LIGHT);
        DEFAULT_BUGFIX_CONFIG = new AutoverBranchConfig();
        DEFAULT_BUGFIX_CONFIG.setNameRegex("bugfix/([A-Z0-9]+-[0-9]+)-.*");
        DEFAULT_BUGFIX_CONFIG.setStopOn(StopOnEnum.ON_FIRST_ANN);
        DEFAULT_PR_CONFIG = new AutoverBranchConfig();
        DEFAULT_PR_CONFIG.setNameRegex("(PR-\\d+)");
        DEFAULT_PR_CONFIG.setStopOn(StopOnEnum.ON_FIRST);
        DEFAULT_OTHER_CONFIG = new AutoverBranchConfig();
        DEFAULT_OTHER_CONFIG.setNameRegex(".*");
        DEFAULT_OTHER_CONFIG.setStopOn(StopOnEnum.ON_FIRST);
    }

    /**
     * The maven extension configuration.
     */
    private final AutoverConfigDecorator config;

    /**
     * Constructor.
     *
     * @param aConfig the maven extension configuration to use
     */
    public ConfigHelper(final AutoverConfigDecorator aConfig) {
        this.config = aConfig;
    }

    /**
     * Get the default maven extension configuration (usually when none was found).
     *
     * @return the default maven extension configuration
     */
    public static AutoverConfig getDefaultConfiguration() {
        final AutoverConfig defaultConfig = new AutoverConfig();
        defaultConfig.setVersionTagRegex(DEFAULT_VERSION_TAG_REGEX);
        final List<AutoverBranchConfig> autoverBranchConfigs = defaultConfig.getAutoverBranchConfigs();
        autoverBranchConfigs.add(DEFAULT_MASTER_CONFIG);
        autoverBranchConfigs.add(DEFAULT_RELEASE_CONFIG);
        autoverBranchConfigs.add(DEFAULT_BUGFIX_CONFIG);
        autoverBranchConfigs.add(DEFAULT_FEATURE_CONFIG);
        autoverBranchConfigs.add(DEFAULT_PR_CONFIG);
        autoverBranchConfigs.add(DEFAULT_OTHER_CONFIG);
        return defaultConfig;
    }

    /**
     * If the artifact with the specified id should be processed (the version should be calculated).
     * we process only if
     * version is not inherited (different than null)
     * groupId is included
     * the version is different than 0.0.0-SNAPSHOT
     *
     * @param version    the version
     * @param groupId    the group id
     * @param artifactId the artifact id
     * @return true if the artifact should be processed and false otherwise
     */
    public boolean shouldProcessArtifact(final String version, final String groupId, final String artifactId) {
        final List<String> includeGroupIds = config.getIncludeGroupIds();
        return ((includeGroupIds.isEmpty() || includeGroupIds.contains(groupId)) && TO_BE_CALCULATED_ARTIFACT_VERSION.equals(version));
    }

    /**
     * Determine the branch configuration that needs to be used.
     *
     * @param branchName the branch name
     * @return the branch configuration
     */
    public AutoverBranchConfigDecorator getBranchConfig(final String branchName) {
        AutoverBranchConfigDecorator autoverBranchConfig = null;
        final List<AutoverBranchConfigDecorator> branchConfigs = config.getAutoverBranchConfigs();
        for (final AutoverBranchConfigDecorator branchConfig : branchConfigs) {
            final Pattern namePattern = branchConfig.getNamePattern();
            final boolean matches = namePattern.matcher(branchName).matches();
            if (matches) {
                autoverBranchConfig = branchConfig;
                break;
            }
        }
        return autoverBranchConfig;
    }

    /**
     * Calculate the version based on the configuration and the git analysis result.
     *
     * @param gitAnalysisResult the git analysis result
     * @return the calculated version
     */
    public String calculateVer(final GitAnalysisResult gitAnalysisResult) {
        final String tagName = gitAnalysisResult.getTagName();
        String calculatedVersion;
        if (gitAnalysisResult.isOnTag()) {
            calculatedVersion = tagName;
        } else {
            if (gitAnalysisResult.isAnnotatedTag()) {
                final LocalizationHelper localizationHelper = new LocalizationHelper();
                final Pattern versionTagRegexPattern = config.getVersionTagPattern();
                final Matcher versionTagRegexMatcher = versionTagRegexPattern.matcher(tagName);
                versionTagRegexMatcher.matches(); //we are sure it matches because it would otherwise not have been selected as a possible tag
                final int groupCount = versionTagRegexMatcher.groupCount();
                String patchVersionStr = null;
                if (groupCount == 0) {
                    throw new RuntimeException(localizationHelper.getMessage(LocalizationHelper.ERR_NO_GROUP_DEFINED_FOR_VERSION_PATCH,
                            config.getVersionTagRegex()));
                } else {
                    patchVersionStr = versionTagRegexMatcher.group(1);
                }
                int patchVersion = -1;
                if ((patchVersionStr == null) || (patchVersionStr.length() == 0)) {
                    throw new RuntimeException(localizationHelper.getMessage(LocalizationHelper.ERR_EMPTY_VERSION_PATCH_DETECTED,
                            config.getVersionTagRegex()));
                } else {
                    try {
                        patchVersion = Integer.parseInt(patchVersionStr);
                    } catch (final NumberFormatException e) {
                        throw new RuntimeException(localizationHelper.getMessage(LocalizationHelper.ERR_INVALID_VERSION_PATCH_DETECTED, patchVersion,
                                config.getVersionTagRegex()));
                    }
                }
                //increase patch number
                patchVersion++;
                //replace the patch number
                calculatedVersion = tagName.substring(0, versionTagRegexMatcher.start(1)) + patchVersion
                        + tagName.substring(versionTagRegexMatcher.end(1));
            } else {
                calculatedVersion = tagName;
            }
            //check if we need to add a branch identifier
            String branchNameQualifier = calculateBranchQualifier(gitAnalysisResult);
            final boolean hasSnapshot = tagName.endsWith(SNAPSHOT_VERSION_SUFFIX);
            if (branchNameQualifier != null) {
                if (hasSnapshot) {
                    calculatedVersion = calculatedVersion.substring(0, calculatedVersion.length() - SNAPSHOT_VERSION_SUFFIX.length())
                            + "-" + branchNameQualifier + SNAPSHOT_VERSION_SUFFIX;
                } else {
                    calculatedVersion += "-" + branchNameQualifier;
                }
            }
            //check if we need to add snapshot
            if (!hasSnapshot && gitAnalysisResult.isAnnotatedTag()) {
                calculatedVersion += SNAPSHOT_VERSION_SUFFIX;
            }
        }
        return calculatedVersion;
    }

    /**
     * Calculate the branch qualifier.
     *
     * @param gitAnalysisResult the git analysis result
     * @return the branch qualifier or null
     */
    private String calculateBranchQualifier(final GitAnalysisResult gitAnalysisResult) {
        final AutoverBranchConfigDecorator branchConfig = gitAnalysisResult.getBranchConfig();
        String branchNameQualifier = null;
        if (branchConfig != null) {
            final Pattern branchNamePattern = branchConfig.getNamePattern();
            final Matcher branchNameMatcher = branchNamePattern.matcher(gitAnalysisResult.getBranchName());
            branchNameMatcher.matches(); //it is sure the this will match
            if (branchNameMatcher.groupCount() > 0) {
                branchNameQualifier = branchNameMatcher.group(1);
                //remove all non letters, numbers and -
                branchNameQualifier = branchNameQualifier.replaceAll(" [^a-zA-Z_0-9\\-.]", "").replaceAll("-+", "_");
            }
        }
        return branchNameQualifier;
    }

    /**
     * Check if the specified tag matches the version tag regex.
     *
     * @param tagShortName the tag
     * @return true if it matches and false otherwise
     */
    public boolean matchesTagRegEx(final String tagShortName) {
        final Pattern versionTagRegexPattern = config.getVersionTagPattern();
        return versionTagRegexPattern.matcher(tagShortName).matches();
    }
}
