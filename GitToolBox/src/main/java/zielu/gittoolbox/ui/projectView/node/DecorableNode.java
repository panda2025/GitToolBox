package zielu.gittoolbox.ui.projectView.node;

import git4idea.repo.GitRepository;
import org.jetbrains.annotations.Nullable;

public interface DecorableNode {
    @Nullable
    GitRepository getRepo();
}
