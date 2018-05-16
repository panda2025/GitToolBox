package zielu.gittoolbox.update;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.update.UpdatedFiles;
import git4idea.config.UpdateMethod;
import git4idea.repo.GitRepository;
import git4idea.update.GitUpdateProcess;
import git4idea.update.GitUpdateResult;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GtUpdateProcess {
  private final GitUpdateProcess updateProcess;

  public GtUpdateProcess(@NotNull Project project,
                         @Nullable ProgressIndicator progressIndicator,
                         @NotNull Collection<GitRepository> repositories) {
    updateProcess = new GitUpdateProcess(project, progressIndicator, repositories,
        UpdatedFiles.create(), true, false);
  }

  @NotNull
  public GitUpdateResult update(@NotNull UpdateMethod updateMethod) {
    return updateProcess.update(updateMethod);
  }
}
