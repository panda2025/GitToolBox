package zielu.gittoolbox.blame;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.actions.VcsAnnotateUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import zielu.gittoolbox.FeatureToggles;
import zielu.intellij.ui.ZUiUtil;

final class BlameUtil {
  private BlameUtil() {
    //do nothing
  }

  static void annotationLock(@NotNull Project project, @NotNull VirtualFile file) {
    if (FeatureToggles.useAnnotationLocks()) {
      ZUiUtil.invokeAndWait(project, () -> VcsAnnotateUtil.getBackgroundableLock(project, file).lock());
    }
  }

  static void annotationUnlock(@NotNull Project project, @NotNull VirtualFile file) {
    if (FeatureToggles.useAnnotationLocks()) {
      ZUiUtil.invokeAndWait(project, () -> VcsAnnotateUtil.getBackgroundableLock(project, file).unlock());
    }
  }
}
