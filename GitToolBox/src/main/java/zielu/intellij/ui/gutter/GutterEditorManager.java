package zielu.intellij.ui.gutter;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiFile;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

class GutterEditorManager {
  private final Map<Editor, GutterAnnotation> annotations = new ConcurrentHashMap<>();
  private final Project project;
  private final Function<Project, GutterAnnotationProvider> providerLocator;

  GutterEditorManager(@NotNull Project project, @NotNull Function<Project, GutterAnnotationProvider> providerLocator) {
    this.project = project;
    this.providerLocator = providerLocator;
  }

  void editorCreated(@NotNull Editor editor, @NotNull PsiFile psiFile) {
    GutterAnnotationProvider provider = annotationProvider();
    annotations.computeIfAbsent(editor, currentEditor -> provider.annotate(psiFile, currentEditor));
  }

  private GutterAnnotationProvider annotationProvider() {
    return providerLocator.apply(project);
  }

  void editorReleased(@NotNull Editor editor) {
    GutterAnnotation removed = annotations.remove(editor);
    if (removed != null) {
      Disposer.dispose(removed);
    }
  }

  void renewEditor(@NotNull Editor editor, @NotNull PsiFile psiFile) {
    annotations.compute(editor, (key, existing) -> {
      if (existing != null) {
        Disposer.dispose(existing);
      }
      return annotationProvider().annotate(psiFile, key);
    });
  }
}
