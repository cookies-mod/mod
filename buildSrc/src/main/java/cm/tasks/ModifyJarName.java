package cm.tasks;

import java.io.IOException;
import org.eclipse.jgit.api.Git;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.jvm.tasks.Jar;

/**
 * Modifies the jar name to include the branch.
 */
public class ModifyJarName implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        target.getTasks().withType(Jar.class).forEach(this::configure);
    }

    private void configure(Jar jar) {
        final Git git = GitUtils.findGit(jar.getProject());

        try {
            final String branchResult = git.getRepository().getBranch();
            if (branchResult == null) return;
            final String branch = branchResult.replaceAll("/", "-");
            if (branch.equalsIgnoreCase("master")) {
                return;
            }
            jar.getArchiveBaseName().set(jar.getArchiveBaseName().get() + "(" + branch +")");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
