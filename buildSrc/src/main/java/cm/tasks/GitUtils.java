package cm.tasks;

import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.gradle.api.Project;

public class GitUtils {

	static Git git;

	public static Git findGit(Project project) {
		if (git == null) {
			git = Git.wrap(findGit(project.getRootProject().getRootDir()));
		}
		return git;
	}

	public static Repository findGit(File path) {
		try {
			return new RepositoryBuilder()
					// --git-dir if supplied, no-op if null
					.readEnvironment() // scan environment GIT_* variables
					.findGitDir() // scan up the file system tree
					.setWorkTree(path).build();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
