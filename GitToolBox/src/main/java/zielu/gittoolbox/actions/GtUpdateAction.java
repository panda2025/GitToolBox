package zielu.gittoolbox.actions;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.GitVcs;
import git4idea.actions.GitRepositoryAction;
import git4idea.config.GitVcsSettings;
import git4idea.config.UpdateMethod;
import git4idea.repo.GitRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import zielu.gittoolbox.ResBundle;
import zielu.gittoolbox.cache.PerRepoInfoCache;
import zielu.gittoolbox.cache.VirtualFileRepoCache;
import zielu.gittoolbox.ui.util.AppUtil;
import zielu.gittoolbox.update.GtUpdateProcess;

public class GtUpdateAction extends GitRepositoryAction {
  @NotNull
  @Override
  protected String getActionName() {
    return ResBundle.getString("action.update");
  }

  @Override
  protected void perform(@NotNull Project project, @NotNull List<VirtualFile> roots,
                         @NotNull VirtualFile defaultRoot) {
    List<GitRepository> reposToUpdate = getReposToUpdate(project, roots);
    GtUpdateDialog dialog = new GtUpdateDialog(project);
    dialog.setRepositories(reposToUpdate);
    if (dialog.showAndGet()) {
      doUpdate(project, reposToUpdate);
    }
  }

  private void doUpdate(@NotNull Project project, @NotNull List<GitRepository> toUpdate) {
    GitVcsSettings settings = GitVcsSettings.getInstance(project);
    UpdateMethod updateType = settings.getUpdateType();

    AppUtil.invokeLaterIfNeeded(() -> GitVcs.runInBackground(new Task.Backgroundable(project,
        ResBundle.getString("action.update.message.updates")) {
      @Override
      public void run(@NotNull ProgressIndicator indicator) {
        GtUpdateProcess process = new GtUpdateProcess(project, indicator, toUpdate);
        process.update(updateType);
        PerRepoInfoCache infoCache = PerRepoInfoCache.getInstance(project);
        infoCache.refresh(toUpdate);
      }
    }));
  }

  private List<GitRepository> getReposToUpdate(@NotNull Project project, @NotNull List<VirtualFile> roots) {
    VirtualFileRepoCache repoCache = VirtualFileRepoCache.getInstance(project);
    PerRepoInfoCache infoCache = PerRepoInfoCache.getInstance(project);
    return roots.stream()
        .map(repoCache::getRepoForRoot)
        .filter(repo -> infoCache.getInfo(repo).status().hasRemoteBranch())
        .collect(Collectors.toList());
  }
}
