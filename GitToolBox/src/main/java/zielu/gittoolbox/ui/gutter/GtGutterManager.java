package zielu.gittoolbox.ui.gutter;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import zielu.gittoolbox.util.AppUtil;
import zielu.intellij.ui.gutter.GutterManager;

class GtGutterManager extends GutterManager {
  GtGutterManager(@NotNull Project project) {
    super(project, prj -> AppUtil.getServiceInstance(prj, TestGutterAnnotationProvider.class));
  }
}
