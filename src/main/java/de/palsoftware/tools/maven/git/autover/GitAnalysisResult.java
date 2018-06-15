package de.palsoftware.tools.maven.git.autover;

/**
 * Result of the git analysis.
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class GitAnalysisResult {

    /**
     * The tag name that was chosen.
     */
    private String tagName;
    /**
     * If the chosen tag is annotated (true) or not (false).
     */
    private boolean annotatedTag;
    /**
     * If the HEAD is on the tag (true) or not (false).
     */
    private boolean onTag;
    /**
     * The name of the current branch.
     */
    private String branchName;
    /**
     * The branch configuration (can be null).
     */
    private AutoverBranchConfigDecorator branchConfig;

    /**
     * Getter.
     *
     * @return the tag name that was chosen
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Setter.
     *
     * @param value the tag name that was chosen
     */
    public void setTagName(final String value) {
        this.tagName = value;
    }

    /**
     * Getter.
     *
     * @return if the chosen tag is annotated (true) or not (false)
     */
    public boolean isAnnotatedTag() {
        return annotatedTag;
    }

    /**
     * Setter.
     *
     * @param value if the chosen tag is annotated (true) or not (false)
     */
    public void setAnnotatedTag(final boolean value) {
        this.annotatedTag = value;
    }

    /**
     * Getter.
     *
     * @return if the HEAD is on the tag (true) or not (false)
     */
    public boolean isOnTag() {
        return onTag;
    }

    /**
     * Setter.
     *
     * @param value if the HEAD is on the tag (true) or not (false)
     */
    public void setOnTag(final boolean value) {
        this.onTag = value;
    }

    /**
     * Getter.
     *
     * @return the name of the current branch
     */
    public String getBranchName() {
        return branchName;
    }

    /**
     * Setter.
     *
     * @param value the name of the current branch
     */
    public void setBranchName(final String value) {
        this.branchName = value;
    }

    /**
     * Getter.
     *
     * @return the baranch configuration (can be null)
     */
    public AutoverBranchConfigDecorator getBranchConfig() {
        return branchConfig;
    }

    /**
     * Setter.
     *
     * @param theBranchConfig the branch configuration
     */
    public void setBranchConfig(final AutoverBranchConfigDecorator theBranchConfig) {
        this.branchConfig = theBranchConfig;
    }
}
