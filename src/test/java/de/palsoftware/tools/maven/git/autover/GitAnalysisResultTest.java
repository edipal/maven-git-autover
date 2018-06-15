package de.palsoftware.tools.maven.git.autover;

import de.palsoftware.tools.maven.git.autover.conf.AutoverBranchConfig;
import de.palsoftware.tools.maven.git.autover.conf.StopOnEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests getters and setters for {@link GitAnalysisResult}
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class GitAnalysisResultTest {

    private GitAnalysisResult gitAnalysisResult;

    @Before
    public void setUp() {
        gitAnalysisResult = new GitAnalysisResult();
    }

    @Test
    public void tagName() {
        String tagName = "AABBCC";
        gitAnalysisResult.setTagName(tagName);
        Assert.assertTrue("GitAnalysisResult -> TagName getter/setter problem!", tagName.equals(gitAnalysisResult.getTagName()));

        tagName = "112233";
        gitAnalysisResult.setTagName(tagName);
        Assert.assertTrue("GitAnalysisResult -> TagName getter/setter problem!", tagName.equals(gitAnalysisResult.getTagName()));
    }

    @Test
    public void annotatedTag() {
        boolean annotatedTag = true;
        gitAnalysisResult.setAnnotatedTag(annotatedTag);
        Assert.assertTrue("GitAnalysisResult -> AnnotatedTag getter/setter problem!", annotatedTag == gitAnalysisResult.isAnnotatedTag());

        annotatedTag = false;
        gitAnalysisResult.setAnnotatedTag(annotatedTag);
        Assert.assertTrue("GitAnalysisResult -> AnnotatedTag getter/setter problem!", annotatedTag == gitAnalysisResult.isAnnotatedTag());
    }

    @Test
    public void onTag() {
        boolean onTag = true;
        gitAnalysisResult.setOnTag(onTag);
        Assert.assertTrue("GitAnalysisResult -> OnTag getter/setter problem!", onTag == gitAnalysisResult.isOnTag());

        onTag = false;
        gitAnalysisResult.setOnTag(onTag);
        Assert.assertTrue("GitAnalysisResult -> OnTag getter/setter problem!", onTag == gitAnalysisResult.isOnTag());
    }

    @Test
    public void getBranchName() {
        String branchName = "AABBCC";
        gitAnalysisResult.setBranchName(branchName);
        Assert.assertTrue("GitAnalysisResult -> BranchName getter/setter problem!", branchName.equals(gitAnalysisResult.getBranchName()));

        branchName = "112233";
        gitAnalysisResult.setBranchName(branchName);
        Assert.assertTrue("GitAnalysisResult -> BranchName getter/setter problem!", branchName.equals(gitAnalysisResult.getBranchName()));
    }

    @Test
    public void getBranchConfig() {
        AutoverBranchConfig branchConfig = new AutoverBranchConfig();
        branchConfig.setNameRegex("ABCDEF");
        branchConfig.setStopOn(StopOnEnum.ON_FIRST);
        AutoverBranchConfigDecorator branchConfigDecorator = new AutoverBranchConfigDecorator(branchConfig);
        gitAnalysisResult.setBranchConfig(branchConfigDecorator);
        Assert.assertTrue("GitAnalysisResult -> BranchConfig getter/setter problem!", branchConfigDecorator.equals(gitAnalysisResult.getBranchConfig()));

        branchConfig = new AutoverBranchConfig();
        branchConfig.setNameRegex("123456");
        branchConfig.setStopOn(StopOnEnum.ON_FIRST_ANN);
        branchConfigDecorator = new AutoverBranchConfigDecorator(branchConfig);
        gitAnalysisResult.setBranchConfig(branchConfigDecorator);
        Assert.assertTrue("GitAnalysisResult -> BranchConfig getter/setter problem!", branchConfigDecorator.equals(gitAnalysisResult.getBranchConfig()));
    }
}