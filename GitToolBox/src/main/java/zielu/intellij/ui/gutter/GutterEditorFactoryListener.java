package zielu.intellij.ui.gutter;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

class GutterEditorFactoryListener implements EditorFactoryListener {
  private final Map<Editor, GutterAnnotation> annotations = new ConcurrentHashMap<>();
  private final Project project;
  private final GutterAnnotationProvider provider;

  GutterEditorFactoryListener(@NotNull Project project, @NotNull GutterAnnotationProvider provider) {
    this.project = project;
    this.provider = provider;
  }

  @Override
  public void editorCreated(@NotNull EditorFactoryEvent event) {
    Editor editor = event.getEditor();
    if (isCurrentProject(editor)) {
      PsiFile psiFile = ReadAction.compute(() -> {
        if (project.isDisposed()) return null;
        return PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
      });
      if (psiFile != null && psiFile.isValid()) {
        annotations.computeIfAbsent(editor, currentEditor -> provider.annotate(psiFile, currentEditor));
      }
    }
  }

  private boolean isCurrentProject(@NotNull Editor editor) {
    return project.equals(editor.getProject());
  }

  @Override
  public void editorReleased(@NotNull EditorFactoryEvent event) {
    Editor editor = event.getEditor();
    if (isCurrentProject(editor)) {
      GutterAnnotation removed = annotations.remove(editor);
      if (removed != null) {
        Disposer.dispose(removed);
      }
    }
  }
}
