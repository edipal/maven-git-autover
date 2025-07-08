package de.palsoftware.tools.maven.git.autover;

import de.palsoftware.tools.maven.git.autover.conf.AutoverBranchConfig;
import de.palsoftware.tools.maven.git.autover.conf.AutoverConfig;
import org.junit.Assert;
import org.junit.Test;

/**
 * Config helper tests.
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class ConfigHelperTest {

    @Test
    public void getDefaultConfiguration() {
        Assert.assertNotNull("ConfigHelper -> getDefaultConfiguration problem!", ConfigHelper.getDefaultConfiguration());
    }

    @Test
    public void shouldProcessArtifact() {
        final AutoverConfig autoverConfig = ConfigHelper.getDefaultConfiguration();
        final ConfigHelper configHelper = new ConfigHelper(new AutoverConfigDecorator(autoverConfig));

        //1. version == null; included group empty
        boolean shouldProcessArtifact = configHelper.shouldProcessArtifact(null, "de.group1", "artifact1");
        Assert.assertFalse("ConfigHelper -> shouldProcessArtifact problem!", shouldProcessArtifact);
        //2. version != 0.0.0-SNAPSHOT; included group empty
        shouldProcessArtifact = configHelper.shouldProcessArtifact("1.0.0", "de.group1", "artifact1");
        Assert.assertFalse("ConfigHelper -> shouldProcessArtifact problem!", shouldProcessArtifact);
        //3. version == 0.0.0-SNAPSHOT; included group empty
        shouldProcessArtifact = configHelper.shouldProcessArtifact("0.0.0-SNAPSHOT", "de.group1", "artifact1");
        Assert.assertTrue("ConfigHelper -> shouldProcessArtifact problem!", shouldProcessArtifact);

        autoverConfig.getIncludeGroupIds().add("de.group3");
        autoverConfig.getIncludeGroupIds().add("de.group2");
        //4. version == null; group not included
        shouldProcessArtifact = configHelper.shouldProcessArtifact(null, "de.group1", "artifact1");
        Assert.assertFalse("ConfigHelper -> shouldProcessArtifact problem!", shouldProcessArtifact);
        //5. version != 0.0.0-SNAPSHOT; group not included
        shouldProcessArtifact = configHelper.shouldProcessArtifact("1.0.0", "de.group1", "artifact1");
        Assert.assertFalse("ConfigHelper -> shouldProcessArtifact problem!", shouldProcessArtifact);
        //6. version == 0.0.0-SNAPSHOT; group not included
        shouldProcessArtifact = configHelper.shouldProcessArtifact("0.0.0-SNAPSHOT", "de.group1", "artifact1");
        Assert.assertFalse("ConfigHelper -> shouldProcessArtifact problem!", shouldProcessArtifact);

        autoverConfig.getIncludeGroupIds().add("de.group1");
        //7. version == null; group included
        shouldProcessArtifact = configHelper.shouldProcessArtifact(null, "de.group1", "artifact1");
        Assert.assertFalse("ConfigHelper -> shouldProcessArtifact problem!", shouldProcessArtifact);
        //8. version != 0.0.0-SNAPSHOT; group included

        shouldProcessArtifact = configHelper.shouldProcessArtifact("1.0.0", "de.group1", "artifact1");
        Assert.assertFalse("ConfigHelper -> shouldProcessArtifact problem!", shouldProcessArtifact);
        //9. version == 0.0.0-SNAPSHOT; group included
        shouldProcessArtifact = configHelper.shouldProcessArtifact("0.0.0-SNAPSHOT", "de.group1", "artifact1");
        Assert.assertTrue("ConfigHelper -> shouldProcessArtifact problem!", shouldProcessArtifact);
    }

    @Test
    public void getBranchConfig() {
        final AutoverConfig autoverConfig = ConfigHelper.getDefaultConfiguration();
        final ConfigHelper configHelper = new ConfigHelper(new AutoverConfigDecorator(autoverConfig));

        //check master
        AutoverBranchConfig branchConfig = ConfigHelper.DEFAULT_MASTER_CONFIG;
        AutoverBranchConfigDecorator foundBranchConfig = configHelper.getBranchConfig("master");
        assertSameBranchConfig(branchConfig, foundBranchConfig);

        //check release
        branchConfig = ConfigHelper.DEFAULT_RELEASE_CONFIG;
        foundBranchConfig = configHelper.getBranchConfig("release/1.0.x");
        assertSameBranchConfig(branchConfig, foundBranchConfig);

        //check feature
        branchConfig = ConfigHelper.DEFAULT_FEATURE_CONFIG;
        foundBranchConfig = configHelper.getBranchConfig("feature/LIB-111-test-junit");
        assertSameBranchConfig(branchConfig, foundBranchConfig);

        //check bugfix
        branchConfig = ConfigHelper.DEFAULT_BUGFIX_CONFIG;
        foundBranchConfig = configHelper.getBranchConfig("bugfix/LIB-112-test-junit");
        assertSameBranchConfig(branchConfig, foundBranchConfig);

        //check PR
        branchConfig = ConfigHelper.DEFAULT_PR_CONFIG;
        foundBranchConfig = configHelper.getBranchConfig("PR-33");
        assertSameBranchConfig(branchConfig, foundBranchConfig);

        //check other
        branchConfig = ConfigHelper.DEFAULT_OTHER_CONFIG;
        foundBranchConfig = configHelper.getBranchConfig("test");
        assertSameBranchConfig(branchConfig, foundBranchConfig);
    }

    private void assertSameBranchConfig(final AutoverBranchConfig branchConfig, final AutoverBranchConfigDecorator foundBranchConfig) {
		Assert.assertEquals("ConfigHelper -> getBranchConfig problem!", branchConfig.getNameRegex(), foundBranchConfig.getNameRegex());
		Assert.assertEquals("ConfigHelper -> getBranchConfig problem!", branchConfig.getStopOn(), foundBranchConfig.getStopOn());
    }

    @Test
    public void calculateVer() {
        final AutoverConfig autoverConfig = ConfigHelper.getDefaultConfiguration();
        final ConfigHelper configHelper = new ConfigHelper(new AutoverConfigDecorator(autoverConfig));
        final GitAnalysisResult gitAnalysisResult = new GitAnalysisResult();
        //on tag | tag snapshot | ann tag | bq
        //T        F              F         F
        gitAnalysisResult.setOnTag(true);
        String tagName = "1.0.0";
        gitAnalysisResult.setTagName(tagName);
        gitAnalysisResult.setAnnotatedTag(false);
        gitAnalysisResult.setBranchName("release/1.1.x");
        gitAnalysisResult.setBranchConfig(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG));
		Assert.assertEquals("ConfigHelper -> calculateVer problem!", "1.0.0", configHelper.calculateVer(gitAnalysisResult));
        //on tag | tag snapshot | ann tag | bq
        //T        F              F         T
        gitAnalysisResult.setOnTag(true);
        tagName = "1.0.0";
        gitAnalysisResult.setTagName(tagName);
        gitAnalysisResult.setAnnotatedTag(false);
        gitAnalysisResult.setBranchName("feature/LIB-111-test-junit");
        gitAnalysisResult.setBranchConfig(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_FEATURE_CONFIG));
		Assert.assertEquals("ConfigHelper -> calculateVer problem!", "1.0.0", configHelper.calculateVer(gitAnalysisResult));
        //on tag | tag snapshot | ann tag | bq
        //T        F              T         F
        gitAnalysisResult.setOnTag(true);
        tagName = "1.0.0";
        gitAnalysisResult.setTagName(tagName);
        gitAnalysisResult.setAnnotatedTag(true);
        gitAnalysisResult.setBranchName("release/1.1.x");
        gitAnalysisResult.setBranchConfig(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG));
		Assert.assertEquals("ConfigHelper -> calculateVer problem!", "1.0.0", configHelper.calculateVer(gitAnalysisResult));
        //on tag | tag snapshot | ann tag | bq
        //T        F              T         T
        gitAnalysisResult.setOnTag(true);
        tagName = "1.0.0";
        gitAnalysisResult.setTagName(tagName);
        gitAnalysisResult.setAnnotatedTag(true);
        gitAnalysisResult.setBranchName("feature/LIB-111-test-junit");
        gitAnalysisResult.setBranchConfig(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_FEATURE_CONFIG));
		Assert.assertEquals("ConfigHelper -> calculateVer problem!", "1.0.0", configHelper.calculateVer(gitAnalysisResult));
        //on tag | tag snapshot | ann tag | bq
        //T        T              F         F
        gitAnalysisResult.setOnTag(true);
        tagName = "1.0.0-SNAPSHOT";
        gitAnalysisResult.setTagName(tagName);
        gitAnalysisResult.setAnnotatedTag(false);
        gitAnalysisResult.setBranchName("release/1.1.x");
        gitAnalysisResult.setBranchConfig(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG));
		Assert.assertEquals("ConfigHelper -> calculateVer problem!", "1.0.0-SNAPSHOT", configHelper.calculateVer(gitAnalysisResult));
        //on tag | tag snapshot | ann tag | bq
        //T        T              F         T
        gitAnalysisResult.setOnTag(true);
        tagName = "1.0.0-SNAPSHOT";
        gitAnalysisResult.setTagName(tagName);
        gitAnalysisResult.setAnnotatedTag(false);
        gitAnalysisResult.setBranchName("feature/LIB-111-test-junit");
        gitAnalysisResult.setBranchConfig(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_FEATURE_CONFIG));
		Assert.assertEquals("ConfigHelper -> calculateVer problem!", "1.0.0-SNAPSHOT", configHelper.calculateVer(gitAnalysisResult));
        //on tag | tag snapshot | ann tag | bq
        //T        T              T         F
        gitAnalysisResult.setOnTag(true);
        tagName = "1.0.0-SNAPSHOT";
        gitAnalysisResult.setTagName(tagName);
        gitAnalysisResult.setAnnotatedTag(true);
        gitAnalysisResult.setBranchName("release/1.1.x");
        gitAnalysisResult.setBranchConfig(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG));
		Assert.assertEquals("ConfigHelper -> calculateVer problem!", "1.0.0-SNAPSHOT", configHelper.calculateVer(gitAnalysisResult));
        //on tag | tag snapshot | ann tag | bq
        //T        T              T         T
        gitAnalysisResult.setOnTag(true);
        tagName = "1.0.0-SNAPSHOT";
        gitAnalysisResult.setTagName(tagName);
        gitAnalysisResult.setAnnotatedTag(true);
        gitAnalysisResult.setBranchName("feature/LIB-111-test-junit");
        gitAnalysisResult.setBranchConfig(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_FEATURE_CONFIG));
		Assert.assertEquals("ConfigHelper -> calculateVer problem!", "1.0.0-SNAPSHOT", configHelper.calculateVer(gitAnalysisResult));
        //on tag | tag snapshot | ann tag | bq
        //F        F              F         F
        gitAnalysisResult.setOnTag(false);
        tagName = "1.0.0";
        gitAnalysisResult.setTagName(tagName);
        gitAnalysisResult.setAnnotatedTag(false);
        gitAnalysisResult.setBranchName("release/1.1.x");
        gitAnalysisResult.setBranchConfig(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG));
		Assert.assertEquals("ConfigHelper -> calculateVer problem!", "1.0.0", configHelper.calculateVer(gitAnalysisResult));
        //on tag | tag snapshot | ann tag | bq
        //F        F              F         T
        gitAnalysisResult.setOnTag(false);
        tagName = "1.0.0";
        gitAnalysisResult.setTagName(tagName);
        gitAnalysisResult.setAnnotatedTag(false);
        gitAnalysisResult.setBranchName("feature/LIB-111-test-junit");
        gitAnalysisResult.setBranchConfig(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_FEATURE_CONFIG));
        Assert.assertEquals("ConfigHelper -> calculateVer problem!", "1.0.0-LIB_111", configHelper.calculateVer(gitAnalysisResult));
        //on tag | tag snapshot | ann tag | bq
        //F        F              T         F
        gitAnalysisResult.setOnTag(false);
        tagName = "1.0.0";
        gitAnalysisResult.setTagName(tagName);
        gitAnalysisResult.setAnnotatedTag(true);
        gitAnalysisResult.setBranchName("release/1.1.x");
        gitAnalysisResult.setBranchConfig(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG));
        Assert.assertEquals("ConfigHelper -> calculateVer problem!", "1.0.1-SNAPSHOT", configHelper.calculateVer(gitAnalysisResult));
        //on tag | tag snapshot | ann tag | bq
        //F        F              T         T
        gitAnalysisResult.setOnTag(false);
        tagName = "1.0.0";
        gitAnalysisResult.setTagName(tagName);
        gitAnalysisResult.setAnnotatedTag(true);
        gitAnalysisResult.setBranchName("feature/LIB-111-test-junit");
        gitAnalysisResult.setBranchConfig(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_FEATURE_CONFIG));
		Assert.assertEquals("ConfigHelper -> calculateVer problem!", "1.0.1-LIB_111-SNAPSHOT", configHelper.calculateVer(gitAnalysisResult));
        //on tag | tag snapshot | ann tag | bq
        //F        T              F         F
        gitAnalysisResult.setOnTag(false);
        tagName = "1.0.0-SNAPSHOT";
        gitAnalysisResult.setTagName(tagName);
        gitAnalysisResult.setAnnotatedTag(false);
        gitAnalysisResult.setBranchName("release/1.1.x");
        gitAnalysisResult.setBranchConfig(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG));
		Assert.assertEquals("ConfigHelper -> calculateVer problem!", "1.0.0-SNAPSHOT", configHelper.calculateVer(gitAnalysisResult));
        //on tag | tag snapshot | ann tag | bq
        //F        T              F         T
        gitAnalysisResult.setOnTag(false);
        tagName = "1.0.0-SNAPSHOT";
        gitAnalysisResult.setTagName(tagName);
        gitAnalysisResult.setAnnotatedTag(false);
        gitAnalysisResult.setBranchName("feature/LIB-111-test-junit");
        gitAnalysisResult.setBranchConfig(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_FEATURE_CONFIG));
		Assert.assertEquals("ConfigHelper -> calculateVer problem!", "1.0.0-LIB_111-SNAPSHOT", configHelper.calculateVer(gitAnalysisResult));
        //on tag | tag snapshot | ann tag | bq
        //F        T              T         F
        gitAnalysisResult.setOnTag(false);
        tagName = "1.0.0-SNAPSHOT";
        gitAnalysisResult.setTagName(tagName);
        gitAnalysisResult.setAnnotatedTag(true);
        gitAnalysisResult.setBranchName("release/1.1.x");
        gitAnalysisResult.setBranchConfig(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG));
		Assert.assertEquals("ConfigHelper -> calculateVer problem!", "1.0.1-SNAPSHOT", configHelper.calculateVer(gitAnalysisResult));
        //on tag | tag snapshot | ann tag | bq
        //F        T              T         T
        gitAnalysisResult.setOnTag(false);
        tagName = "1.0.0-SNAPSHOT";
        gitAnalysisResult.setTagName(tagName);
        gitAnalysisResult.setAnnotatedTag(true);
        gitAnalysisResult.setBranchName("feature/LIB-111-test-junit");
        gitAnalysisResult.setBranchConfig(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_FEATURE_CONFIG));
		Assert.assertEquals("ConfigHelper -> calculateVer problem!", "1.0.1-LIB_111-SNAPSHOT", configHelper.calculateVer(gitAnalysisResult));

        //on tag | tag snapshot | ann tag | bq
        //T        F              T         T
        autoverConfig.setVersionTagRegex("(?:release[\\/])?([0-9]+\\.[0-9]+\\.([0-9]+)(-SNAPSHOT)?)");
        ConfigHelper newConfigHelper = new ConfigHelper(new AutoverConfigDecorator(autoverConfig));
        gitAnalysisResult.setOnTag(true);
        tagName = "release/1.0.0";
        gitAnalysisResult.setTagName(tagName);
        gitAnalysisResult.setAnnotatedTag(true);
        gitAnalysisResult.setBranchName("feature/LIB-111-test-junit");
        gitAnalysisResult.setBranchConfig(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_FEATURE_CONFIG));
		Assert.assertEquals("ConfigHelper -> calculateVer problem!", "1.0.0", newConfigHelper.calculateVer(gitAnalysisResult));
        //on tag | tag snapshot | ann tag | bq
        //T        F              T         T
        autoverConfig.setVersionTagRegex("(?:release[\\/])?([0-9]+\\.[0-9]+\\.([0-9]+)(-SNAPSHOT)?)");
        newConfigHelper = new ConfigHelper(new AutoverConfigDecorator(autoverConfig));
        gitAnalysisResult.setOnTag(false);
        tagName = "release/1.0.0";
        gitAnalysisResult.setTagName(tagName);
        gitAnalysisResult.setAnnotatedTag(true);
        gitAnalysisResult.setBranchName("release/1.1.x");
        gitAnalysisResult.setBranchConfig(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG));
		Assert.assertEquals("ConfigHelper -> calculateVer problem!", "1.0.1-SNAPSHOT", newConfigHelper.calculateVer(gitAnalysisResult));
    }

    @Test
    public void calculateVer_exceptions() {

        //no group count
        GitAnalysisResult gitAnalysisResult = new GitAnalysisResult();
        gitAnalysisResult.setOnTag(false);
        String tagName = "1.0.0";
        gitAnalysisResult.setTagName(tagName);
        gitAnalysisResult.setAnnotatedTag(true);
        gitAnalysisResult.setBranchName("release/1.1.x");
        gitAnalysisResult.setBranchConfig(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG));

        AutoverConfig autoverConfig = ConfigHelper.getDefaultConfiguration();
        autoverConfig.setVersionTagRegex("[0-9]+\\.[0-9]+\\.[0-9]+");
        ConfigHelper configHelper = new ConfigHelper(new AutoverConfigDecorator(autoverConfig));

        try {
            configHelper.calculateVer(gitAnalysisResult);
            Assert.fail("ConfigHelper -> calculateVer problem!");
        } catch (Exception e) {
            //OK
        }

        //empty patch version string
        gitAnalysisResult = new GitAnalysisResult();
        gitAnalysisResult.setOnTag(false);
        tagName = "1.0-SNAPSHOT";
        gitAnalysisResult.setTagName(tagName);
        gitAnalysisResult.setAnnotatedTag(true);
        gitAnalysisResult.setBranchName("release/1.1.x");
        gitAnalysisResult.setBranchConfig(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG));

        autoverConfig = ConfigHelper.getDefaultConfiguration();
        autoverConfig.setVersionTagRegex("[0-9]+\\.[0-9]+(.*)-SNAPSHOT");
        configHelper = new ConfigHelper(new AutoverConfigDecorator(autoverConfig));

        try {
            configHelper.calculateVer(gitAnalysisResult);
            Assert.fail("ConfigHelper -> calculateVer problem!");
        } catch (Exception e) {
            //OK
        }

        //patch version not a number
        gitAnalysisResult = new GitAnalysisResult();
        gitAnalysisResult.setOnTag(false);
        tagName = "1.0.beta-SNAPSHOT";
        gitAnalysisResult.setTagName(tagName);
        gitAnalysisResult.setAnnotatedTag(true);
        gitAnalysisResult.setBranchName("release/1.1.x");
        gitAnalysisResult.setBranchConfig(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG));

        autoverConfig = ConfigHelper.getDefaultConfiguration();
        autoverConfig.setVersionTagRegex("[0-9]+\\.[0-9]+\\.(.*)-SNAPSHOT");
        configHelper = new ConfigHelper(new AutoverConfigDecorator(autoverConfig));

        try {
            configHelper.calculateVer(gitAnalysisResult);
            Assert.fail("ConfigHelper -> calculateVer problem!");
        } catch (Exception e) {
            //OK
        }
    }

    @Test
    public void matchesTagRegEx() {
        final AutoverConfig autoverConfig = ConfigHelper.getDefaultConfiguration();
        final ConfigHelper configHelper = new ConfigHelper(new AutoverConfigDecorator(autoverConfig));

        //1.1.1
        Assert.assertTrue("ConfigHelper -> matchesTagRegEx problem!", configHelper.matchesTagRegEx("1.1.1"));
        //1.1.1-SNAPSHOT
        Assert.assertTrue("ConfigHelper -> matchesTagRegEx problem!", configHelper.matchesTagRegEx("1.1.1-SNAPSHOT"));
        //1.1
        Assert.assertFalse("ConfigHelper -> matchesTagRegEx problem!", configHelper.matchesTagRegEx("1.1"));
    }
}