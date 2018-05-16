package zielu.gittoolbox.ui.common;

import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import git4idea.repo.GitRepository;
import javax.swing.JList;
import jodd.util.StringBand;
import org.jetbrains.annotations.NotNull;
import zielu.gittoolbox.util.GtUtil;

public class RepoListCellRenderer extends ColoredListCellRenderer<GitRepository> {
  @Override
  protected void customizeCellRenderer(@NotNull JList<? extends GitRepository> list, GitRepository value, int index,
                                       boolean selected, boolean hasFocus) {
    append(GtUtil.name(value));
    StringBand url = new StringBand(" (");
    url.append(value.getRoot().getPresentableUrl());
    url.append(")");
    append(url.toString(), SimpleTextAttributes.GRAYED_ATTRIBUTES);
  }
}
