package zielu.gittoolbox.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import git4idea.repo.GitRepository;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.jetbrains.annotations.Nullable;
import zielu.gittoolbox.ResBundle;
import zielu.gittoolbox.ui.common.RepoListCellRenderer;
import zielu.gittoolbox.util.GtUtil;

class GtUpdateDialog extends DialogWrapper {
  private JPanel centerPanel;
  private JBList<GitRepository> repoList;
  private List<GitRepository> repositories = new ArrayList<>();

  GtUpdateDialog(@Nullable Project project) {
    super(project);
    centerPanel = new JPanel(new BorderLayout());
    repoList = new JBList<>();
    repoList.setCellRenderer(new RepoListCellRenderer());
    JBScrollPane scrollPane = new JBScrollPane(repoList);
    centerPanel.add(scrollPane, BorderLayout.CENTER);
    setTitle(ResBundle.getString("action.update.message.title"));
    init();
  }

  private void fillData() {
    List<GitRepository> repositoriesToShow = new ArrayList<>(repositories);
    repositoriesToShow = GtUtil.sort(repositoriesToShow);
    repoList.setModel(new CollectionListModel<>(repositoriesToShow));
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    return centerPanel;
  }

  public void setRepositories(List<GitRepository> repositories) {
    this.repositories = new ArrayList<>(repositories);
  }

  @Override
  public void show() {
    fillData();
    super.show();
  }
}
