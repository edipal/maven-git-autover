package de.palsoftware.tools.maven.git.autover;

import de.palsoftware.tools.maven.git.autover.conf.AutoverConfig;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.junit.JGitTestUtil;
import org.eclipse.jgit.junit.RepositoryTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Main test case for {@link GitHelper}
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class GitHelperTest extends RepositoryTestCase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void analyze1() throws GitAPIException, IOException {

        //                               |
        //                               |            o file 11 commit (1.1.0 - annotated)
        //                               |          /
        //                 Merge commit  o        /
        //                             / |      /
        //                           /   |    /
        //                         /     |  /
        //                       /       |/     o Merge commit
        //     file 11 commit  o         |      |  \
        //                       \       |      |   \
        //                         \     |      |    \
        //                           \   |      |     \
        //                             \ |      |      \
        // (1.2.0-SNAPSHOT) Merge commit o------|-------o file 10 commit
        //                               |      |       |
        //                               |      |       |
        //                  Merge commit o      |       |
        //                            /  |      |       |
        //                          /    |      |       |
        //                        /      |      |       o file 9 commit
        //     file 8 commit     o       |      |     /
        //                       |       |      |   /
        //     file 7 commit     o       |      | /
        //                        \      |      o Merge commit
        //                         \     |      | \
        //                          \    |      |  \
        //                           \   |      |   \
        //                            \  |      |    \
        //                             \ |      |     \
        //                  Merge commit o -----|----- o file6 commit
        //                               |      |    /
        //                               |      |  /
        //                               |      |/
        //                               |      o file5 commit (1.0.0 - annotated)
        //                               |      |
        //                               |      o file4 commit
        //                               |    /
        //                               |  /
        //                               |/
        //                               o file3 commit (1.1.0-SNAPSHOT - not annotated)
        //                               |
        //                               o file2 commit
        //                               |
        //                               o file1 commit (1.0.0-SNAPSHOT - not annotated)
        final Git git = new Git(db);
        final File gitRepoDir = git.getRepository().getDirectory();

        final AutoverConfig config = ConfigHelper.getDefaultConfiguration();
        final AutoverConfigDecorator configDecorator = new AutoverConfigDecorator(config);
        final GitHelper gh = new GitHelper(gitRepoDir, new ConfigHelper(configDecorator));

        //initial commit
        git.commit().setMessage("initial commit").call();

        //file 1 - master
        writeTrashFile("file1", "file1content");
        git.add().addFilepattern("file1").call();
        git.commit().setMessage("file1 commit").call();

        //tag 1.0.0-SNAPSHOT (not annotated)
        git.tag().setAnnotated(false).setName("1.0.0-SNAPSHOT").call();

        GitAnalysisResult gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertTrue(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.0-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("master".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_MASTER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 2 - master
        writeTrashFile("file2", "file2content");
        git.add().addFilepattern("file2").call();
        git.commit().setMessage("file2 commit").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.0-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("master".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_MASTER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 3 - master
        writeTrashFile("file3", "file3content");
        git.add().addFilepattern("file3").call();
        git.commit().setMessage("file3 commit").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.0-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("master".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_MASTER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //branch release/1.0.x
        git.branchCreate().setName("release/1.0.x").call();
        git.checkout().setName("release/1.0.x").call();

        //file 4 - release/1.0.x
        writeTrashFile("file4", "file4content");
        git.add().addFilepattern("file4").call();
        git.commit().setMessage("file4 commit").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertNull(gitAnalysisResult.getTagName());
        Assert.assertTrue("release/1.0.x".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 5 - release/1.0.x
        writeTrashFile("file5", "file5content");
        git.add().addFilepattern("file5").call();
        git.commit().setMessage("file5 commit").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertNull(gitAnalysisResult.getTagName());
        Assert.assertTrue("release/1.0.x".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //tag 1.0.0 (annotated) - release/1.0.x
        git.tag().setAnnotated(true).setName("1.0.0").setMessage("Released version 1.0.0").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertTrue(gitAnalysisResult.isAnnotatedTag());
        Assert.assertTrue(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.0".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("release/1.0.x".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //branch master
        git.checkout().setName("master").call();

        //tag 1.1.0-SNAPSHOT (not annotated) - master
        git.tag().setAnnotated(false).setName("1.1.0-SNAPSHOT").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertTrue(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.1.0-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("master".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_MASTER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //branch release/1.0.x
        git.checkout().setName("release/1.0.x").call();

        //branch bugfix/ABC-111-just-a-dummy-bug
        git.branchCreate().setName("bugfix/ABC-111-just-a-dummy-bug").call();
        git.checkout().setName("bugfix/ABC-111-just-a-dummy-bug").call();

        //file 6 - bugfix/ABC-111-just-a-dummy-bug
        writeTrashFile("file6", "file6content");
        git.add().addFilepattern("file6").call();
        git.commit().setMessage("file6 commit").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertTrue(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.0".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("bugfix/ABC-111-just-a-dummy-bug".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_BUGFIX_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //branch release/1.0.x
        git.checkout().setName("release/1.0.x").call();

        //merge bugfix/ABC-111-just-a-dummy-bug into release/1.0.x
        git.merge().setFastForward(MergeCommand.FastForwardMode.NO_FF).include(db.exactRef("refs/heads/bugfix/ABC-111-just-a-dummy-bug")).call();

        gitAnalysisResult = gh.analyze();

        Assert.assertTrue(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.0".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("release/1.0.x".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //branch master
        git.checkout().setName("master").call();

        //merge bugfix/ABC-111-just-a-dummy-bug into master
        git.merge().setFastForward(MergeCommand.FastForwardMode.NO_FF).include(db.exactRef("refs/heads/bugfix/ABC-111-just-a-dummy-bug")).call();

        gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.1.0-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("master".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_MASTER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //branch feature/ABC-111-just-a-dummy-feature
        git.branchCreate().setName("feature/ABC-111-just-a-dummy-feature").call();
        git.checkout().setName("feature/ABC-111-just-a-dummy-feature").call();

        //file 7 - feature/ABC-111-just-a-dummy-feature
        writeTrashFile("file7", "file7content");
        git.add().addFilepattern("file7").call();
        git.commit().setMessage("file7 commit").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.1.0-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("feature/ABC-111-just-a-dummy-feature".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_FEATURE_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 8 - feature/ABC-111-just-a-dummy-feature
        writeTrashFile("file8", "file8content");
        git.add().addFilepattern("file8").call();
        git.commit().setMessage("file8 commit").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.1.0-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("feature/ABC-111-just-a-dummy-feature".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_FEATURE_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //branch master
        git.checkout().setName("master").call();

        //merge bugfix/ABC-111-just-a-dummy-bug into master
        git.merge().setFastForward(MergeCommand.FastForwardMode.NO_FF).include(db.exactRef("refs/heads/feature/ABC-111-just-a-dummy-feature")).call();

        gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.1.0-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("master".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_MASTER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //branch release/1.0.x
        git.checkout().setName("release/1.0.x").call();

        //branch bugfix/ABC-112-another-dummy-bug
        git.branchCreate().setName("bugfix/ABC-112-another-dummy-bug").call();
        git.checkout().setName("bugfix/ABC-112-another-dummy-bug").call();

        //file 9 - bugfix/ABC-112-another-dummy-bug
        writeTrashFile("file9", "file9content");
        git.add().addFilepattern("file9").call();
        git.commit().setMessage("file9 commit").call();

        //file 10 - bugfix/ABC-112-another-dummy-bug
        writeTrashFile("file10", "file10content");
        git.add().addFilepattern("file10").call();
        git.commit().setMessage("file10 commit").call();

        //branch master
        git.checkout().setName("master").call();

        //merge bugfix/ABC-112-another-dummy-bug into master
        git.merge().setFastForward(MergeCommand.FastForwardMode.NO_FF).include(db.exactRef("refs/heads/bugfix/ABC-112-another-dummy-bug")).call();

        gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.1.0-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("master".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_MASTER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //branch release/1.0.x
        git.checkout().setName("release/1.0.x").call();

        //merge bugfix/ABC-112-another-dummy-bug into release/1.0.x
        git.merge().setFastForward(MergeCommand.FastForwardMode.NO_FF).include(db.exactRef("refs/heads/bugfix/ABC-112-another-dummy-bug")).call();

        gitAnalysisResult = gh.analyze();

        Assert.assertTrue(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.0".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("release/1.0.x".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //tag 1.0.1 (annotated) - release/1.0.x
        git.tag().setAnnotated(true).setName("1.0.1").setMessage("Released version 1.0.1").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertTrue(gitAnalysisResult.isAnnotatedTag());
        Assert.assertTrue(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.1".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("release/1.0.x".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //branch master
        git.checkout().setName("master").call();

        //tag 1.2.0-SNAPSHOT (not annotated) - master
        git.tag().setAnnotated(false).setName("1.2.0-SNAPSHOT").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertTrue(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.2.0-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("master".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_MASTER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //branch feature/ABC-112-another-dummy-feature
        git.branchCreate().setName("feature/ABC-112-another-dummy-feature").call();
        git.checkout().setName("feature/ABC-112-another-dummy-feature").call();

        //file 11 - feature/ABC-112-another-dummy-feature
        writeTrashFile("file11", "file11content");
        git.add().addFilepattern("file11").call();
        git.commit().setMessage("file11 commit").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.2.0-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("feature/ABC-112-another-dummy-feature".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_FEATURE_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //branch master
        git.checkout().setName("master").call();

        //merge feature/ABC-112-another-dummy-feature into master
        git.merge().setFastForward(MergeCommand.FastForwardMode.NO_FF).include(db.exactRef("refs/heads/feature/ABC-112-another-dummy-feature")).call();

        gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.2.0-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("master".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_MASTER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));
    }

    @Test
    public void analyze2() throws GitAPIException, IOException {

        //   o file7 commit (abc - not annotated)
        //   |
        //   o file6 commit
        //   |
        //   o file5 commit (1.1.0-SNAPSHOT - not annotated)
        //   |
        //   o file4 commit (1.0.1 - annotated)
        //   |
        //   o file3 commit (1.0.0 - annotated)
        //   |
        //   o file2 commit
        //   |
        //   o file1 commit (1.0.0-SNAPSHOT - not annotated)

        final FileRepository db1 = createWorkRepository();
        final Git git = new Git(db1);
        final File gitRepoDir = git.getRepository().getDirectory();

        final AutoverConfig config = ConfigHelper.getDefaultConfiguration();
        final AutoverConfigDecorator configDecorator = new AutoverConfigDecorator(config);
        final GitHelper gh = new GitHelper(gitRepoDir, new ConfigHelper(configDecorator));

        //initial commit
        git.commit().setMessage("initial commit").call();

        //file 1 - master
        JGitTestUtil.writeTrashFile(db1, "file1", "file1content");
        git.add().addFilepattern("file1").call();
        git.commit().setMessage("file1 commit").call();

        //tag 1.0.0-SNAPSHOT (not annotated)
        git.tag().setAnnotated(false).setName("1.0.0-SNAPSHOT").call();

        GitAnalysisResult gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertTrue(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.0-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("master".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_MASTER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 2 - master
        JGitTestUtil.writeTrashFile(db1, "file2", "file2content");
        git.add().addFilepattern("file2").call();
        git.commit().setMessage("file2 commit").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.0-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("master".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_MASTER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 3 - master
        JGitTestUtil.writeTrashFile(db1, "file3", "file3content");
        git.add().addFilepattern("file3").call();
        git.commit().setMessage("file3 commit").call();

        //tag 1.0.0 (annotated)
        git.tag().setAnnotated(true).setName("1.0.0").setMessage("Release version 1.0.0").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.0-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("master".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_MASTER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 4 - master
        JGitTestUtil.writeTrashFile(db1, "file4", "file4content");
        git.add().addFilepattern("file4").call();
        git.commit().setMessage("file4 commit").call();

        //tag 1.0.1 (annotated)
        git.tag().setAnnotated(true).setName("1.0.1").setMessage("Release version 1.0.1").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.0-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("master".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_MASTER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 5 - master
        JGitTestUtil.writeTrashFile(db1, "file5", "file5content");
        git.add().addFilepattern("file5").call();
        git.commit().setMessage("file5 commit").call();

        //tag 1.1.0-SNAPSHOT (not annotated)
        git.tag().setAnnotated(false).setName("1.1.0-SNAPSHOT").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertTrue(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.1.0-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("master".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_MASTER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 6 - master
        JGitTestUtil.writeTrashFile(db1, "file6", "file6content");
        git.add().addFilepattern("file6").call();
        git.commit().setMessage("file6 commit").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.1.0-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("master".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_MASTER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 7 - master
        JGitTestUtil.writeTrashFile(db1, "file7", "file7ontent");
        git.add().addFilepattern("file7").call();
        git.commit().setMessage("file7 commit").call();

        //tag abc (not annotated)
        git.tag().setAnnotated(false).setName("abc").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.1.0-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("master".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_MASTER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));
    }

    @Test
    public void analyze3() throws GitAPIException, IOException {

        //   o file7 commit (abc - not annotated)
        //   |
        //   o file6 commit
        //   |
        //   o file5 commit (1.0.2 - annotated)
        //   |
        //   o file4 commit (1.0.1 - annotated)
        //   |
        //   o file3 commit (1.0.1-SNAPSHOT - not annotated)
        //   |
        //   o file2 commit
        //   |
        //   o file1 commit (1.0.0 - annotated)

        final FileRepository db1 = createWorkRepository();
        final Git git = new Git(db1);
        final File gitRepoDir = git.getRepository().getDirectory();

        final AutoverConfig config = ConfigHelper.getDefaultConfiguration();
        final AutoverConfigDecorator configDecorator = new AutoverConfigDecorator(config);
        final GitHelper gh = new GitHelper(gitRepoDir, new ConfigHelper(configDecorator));

        //initial commit
        git.commit().setMessage("initial commit").call();

        //branch release/1.0.x
        git.branchCreate().setName("release/1.0.x").call();
        git.checkout().setName("release/1.0.x").call();

        //file 1 - release/1.0.x
        JGitTestUtil.writeTrashFile(db1, "file1", "file1content");
        git.add().addFilepattern("file1").call();
        git.commit().setMessage("file1 commit").call();

        //tag 1.0.0 (annotated)
        git.tag().setAnnotated(true).setName("1.0.0").setMessage("Release version 1.0.0").call();

        GitAnalysisResult gitAnalysisResult = gh.analyze();

        Assert.assertTrue(gitAnalysisResult.isAnnotatedTag());
        Assert.assertTrue(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.0".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("release/1.0.x".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 2 - release/1.0.x
        JGitTestUtil.writeTrashFile(db1, "file2", "file2content");
        git.add().addFilepattern("file2").call();
        git.commit().setMessage("file2 commit").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertTrue(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.0".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("release/1.0.x".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 3 - release/1.0.x
        JGitTestUtil.writeTrashFile(db1, "file3", "file3content");
        git.add().addFilepattern("file3").call();
        git.commit().setMessage("file3 commit").call();

        //tag 1.0.1-SNAPSHOT (not annotated)
        git.tag().setAnnotated(false).setName("1.0.1-SNAPSHOT").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertTrue(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.0".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("release/1.0.x".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 4 - release/1.0.x
        JGitTestUtil.writeTrashFile(db1, "file4", "file4content");
        git.add().addFilepattern("file4").call();
        git.commit().setMessage("file4 commit").call();

        //tag 1.0.1 (annotated)
        git.tag().setAnnotated(true).setName("1.0.1").setMessage("Release version 1.0.1").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertTrue(gitAnalysisResult.isAnnotatedTag());
        Assert.assertTrue(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.1".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("release/1.0.x".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 5 - release/1.0.x
        JGitTestUtil.writeTrashFile(db1, "file5", "file5content");
        git.add().addFilepattern("file5").call();
        git.commit().setMessage("file5 commit").call();

        //tag 1.0.2 (annotated)
        git.tag().setAnnotated(true).setName("1.0.2").setMessage("Release version 1.0.2").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertTrue(gitAnalysisResult.isAnnotatedTag());
        Assert.assertTrue(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.2".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("release/1.0.x".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 6 - release/1.0.x
        JGitTestUtil.writeTrashFile(db1, "file6", "file6content");
        git.add().addFilepattern("file6").call();
        git.commit().setMessage("file6 commit").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertTrue(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.2".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("release/1.0.x".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 7 - release/1.0.x
        JGitTestUtil.writeTrashFile(db1, "file7", "file7content");
        git.add().addFilepattern("file7").call();
        git.commit().setMessage("file7 commit").call();

        //tag abc (not annotated)
        git.tag().setAnnotated(false).setName("abc").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertTrue(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.2".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("release/1.0.x".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_RELEASE_CONFIG).equals(gitAnalysisResult.getBranchConfig()));
    }

    @Test
    public void analyze4() throws GitAPIException, IOException {

        //   o file7 commit (abc - not annotated)
        //   |
        //   o file6 commit
        //   |
        //   o file5 commit (1.0.2-SNAPSHOT - not annotated)
        //   |
        //   o file4 commit (1.0.1 - annotated)
        //   |
        //   o file3 commit (1.0.1-SNAPSHOT - not annotated)
        //   |
        //   o file2 commit
        //   |
        //   o file1 commit (1.0.0 - annotated)

        final FileRepository db1 = createWorkRepository();
        final Git git = new Git(db1);
        final File gitRepoDir = git.getRepository().getDirectory();

        final AutoverConfig config = ConfigHelper.getDefaultConfiguration();
        final AutoverConfigDecorator configDecorator = new AutoverConfigDecorator(config);
        final GitHelper gh = new GitHelper(gitRepoDir, new ConfigHelper(configDecorator));

        //initial commit
        git.commit().setMessage("initial commit").call();

        //branch dummybranch
        git.branchCreate().setName("dummybranch").call();
        git.checkout().setName("dummybranch").call();

        //file 1 - dummybranch
        JGitTestUtil.writeTrashFile(db1, "file1", "file1content");
        git.add().addFilepattern("file1").call();
        git.commit().setMessage("file1 commit").call();

        //tag 1.0.0 (annotated)
        git.tag().setAnnotated(true).setName("1.0.0").setMessage("Release version 1.0.0").call();

        GitAnalysisResult gitAnalysisResult = gh.analyze();

        Assert.assertTrue(gitAnalysisResult.isAnnotatedTag());
        Assert.assertTrue(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.0".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("dummybranch".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_OTHER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 2 - dummybranch
        JGitTestUtil.writeTrashFile(db1, "file2", "file2content");
        git.add().addFilepattern("file2").call();
        git.commit().setMessage("file2 commit").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertTrue(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.0".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("dummybranch".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_OTHER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 3 - dummybranch
        JGitTestUtil.writeTrashFile(db1, "file3", "file3content");
        git.add().addFilepattern("file3").call();
        git.commit().setMessage("file3 commit").call();

        //tag 1.0.1-SNAPSHOT (not annotated)
        git.tag().setAnnotated(false).setName("1.0.1-SNAPSHOT").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertTrue(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.1-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("dummybranch".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_OTHER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 4 - dummybranch
        JGitTestUtil.writeTrashFile(db1, "file4", "file4content");
        git.add().addFilepattern("file4").call();
        git.commit().setMessage("file4 commit").call();

        //tag 1.0.1 (annotated)
        git.tag().setAnnotated(true).setName("1.0.1").setMessage("Release version 1.0.1").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertTrue(gitAnalysisResult.isAnnotatedTag());
        Assert.assertTrue(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.1".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("dummybranch".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_OTHER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 5 - dummybranch
        JGitTestUtil.writeTrashFile(db1, "file5", "file5content");
        git.add().addFilepattern("file5").call();
        git.commit().setMessage("file5 commit").call();

        //tag 1.0.2-SNAPSHOT (not annotated)
        git.tag().setAnnotated(false).setName("1.0.2-SNAPSHOT").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertTrue(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.2-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("dummybranch".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_OTHER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 6 - dummybranch
        JGitTestUtil.writeTrashFile(db1, "file6", "file6content");
        git.add().addFilepattern("file6").call();
        git.commit().setMessage("file6 commit").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.2-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("dummybranch".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_OTHER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));

        //file 7 - dummybranch
        JGitTestUtil.writeTrashFile(db1, "file7", "file7content");
        git.add().addFilepattern("file7").call();
        git.commit().setMessage("file7 commit").call();

        //tag abc (not annotated)
        git.tag().setAnnotated(false).setName("abc").call();

        gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertTrue("1.0.2-SNAPSHOT".equals(gitAnalysisResult.getTagName()));
        Assert.assertTrue("dummybranch".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_OTHER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));
    }

    @Test
    public void analyze5() throws GitAPIException, IOException {

        final FileRepository db1 = createWorkRepository();
        final Git git = new Git(db1);
        final File gitRepoDir = git.getRepository().getDirectory();

        final AutoverConfig config = ConfigHelper.getDefaultConfiguration();
        final AutoverConfigDecorator configDecorator = new AutoverConfigDecorator(config);
        final GitHelper gh = new GitHelper(gitRepoDir, new ConfigHelper(configDecorator));

        //initial commit
        git.commit().setMessage("initial commit").call();

        final GitAnalysisResult gitAnalysisResult = gh.analyze();

        Assert.assertFalse(gitAnalysisResult.isAnnotatedTag());
        Assert.assertFalse(gitAnalysisResult.isOnTag());
        Assert.assertNull(gitAnalysisResult.getTagName());
        Assert.assertTrue("master".equals(gitAnalysisResult.getBranchName()));
        Assert.assertTrue(new AutoverBranchConfigDecorator(ConfigHelper.DEFAULT_MASTER_CONFIG).equals(gitAnalysisResult.getBranchConfig()));
    }
}