package de.palsoftware.tools.maven.git.autover;

import de.palsoftware.tools.maven.git.autover.conf.StopOnEnum;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper to do the git analysis.
 *
 * @author <a href="mailto:edi@pal-software.de">Eduard Pal</a>
 * @since 2018-06-15
 */
public class GitHelper {

    /**
     * The repository folder.
     */
    private final File repositoryFolder;
    /**
     * The configuration helper.
     */
    private final ConfigHelper configHelper;

    /**
     * Constructor.
     *
     * @param aRepositoryFolder the repository folder
     * @param aConfigHelper     the configuration helper
     */
    public GitHelper(final File aRepositoryFolder, final ConfigHelper aConfigHelper) {
        this.repositoryFolder = aRepositoryFolder;
        this.configHelper = aConfigHelper;
    }

    /**
     * Do the git repository analysis.
     *
     * @return the analysis result
     * @throws IOException for IO problems
     */
    public GitAnalysisResult analyze() throws IOException {
        final GitAnalysisResult gitAnalysisResult = new GitAnalysisResult();
        final Map<String, ObjectId> annotatedTags = new HashMap<>();
        final Map<String, ObjectId> lightweightTags = new HashMap<>();
        final FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try (Repository repository = builder.findGitDir(repositoryFolder).build()) {
            final ObjectId rootId = repository.resolve("HEAD");
            final String branchName = repository.getBranch();
            gitAnalysisResult.setBranchName(branchName);
            final AutoverBranchConfigDecorator autoverBranchConfig = configHelper.getBranchConfig(branchName);
            gitAnalysisResult.setBranchConfig(autoverBranchConfig);
            StopOnEnum stopOn = StopOnEnum.ON_FIRST;
            if (autoverBranchConfig != null) {
                stopOn = autoverBranchConfig.getStopOn();
            }
            try (RevWalk revWalk = new RevWalk(repository)) {
                final RevCommit rootCommit = revWalk.parseCommit(rootId);
                revWalk.markStart(rootCommit);
                final Map<String, Ref> refList = repository.getTags();
                for (final String tag : refList.keySet()) {
                    final String tagShortName = Repository.shortenRefName(tag);
                    final boolean matchesTagRegEx = configHelper.matchesTagRegEx(tagShortName);
                    if (!matchesTagRegEx) {
                        continue;
                    }
                    final Ref tagRef = refList.get(tag);
                    final RevObject tagRevObject = revWalk.parseAny(tagRef.getObjectId());
                    if (tagRevObject instanceof RevTag) {
                        annotatedTags.put(tagShortName, ((RevTag) tagRevObject).getObject().getId());
                    } else if (tagRevObject instanceof RevCommit) {
                        lightweightTags.put(tagShortName, tagRevObject.getId());
                    }
                }
                String foundTagName = null;
                for (RevCommit commit : revWalk) {
                    final ObjectId commitId = commit.getId();
                    for (final String annotatedTag : annotatedTags.keySet()) {
                        final ObjectId tagId = annotatedTags.get(annotatedTag);
                        if (tagId.equals(commitId) && (stopOn != StopOnEnum.ON_FIRST_LIGHT)) {
                            foundTagName = annotatedTag;
                            gitAnalysisResult.setTagName(foundTagName);
                            gitAnalysisResult.setAnnotatedTag(true);
                            break;
                        }
                    }
                    for (final String lightweightTag : lightweightTags.keySet()) {
                        final ObjectId tagId = lightweightTags.get(lightweightTag);
                        if (tagId.equals(commitId) && (stopOn != StopOnEnum.ON_FIRST_ANN)) {
                            foundTagName = lightweightTag;
                            gitAnalysisResult.setTagName(foundTagName);
                            gitAnalysisResult.setAnnotatedTag(false);
                            break;
                        }
                    }
                    if (foundTagName != null) {
                        final boolean isOnTag = rootId.equals(commitId);
                        gitAnalysisResult.setOnTag(isOnTag);
                        break;
                    }
                }
            }
        }
        return gitAnalysisResult;
    }
}
