package zielu.intellij.ui.gutter;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

class GutterEditorFactoryListener implements EditorFactoryListener {
  private final Map<Editor, GutterAnnotation> annotations = new ConcurrentHashMap<>();
  private final Project project;
  private final GutterEditorManager gutterEditorManager;

  GutterEditorFactoryListener(@NotNull Project project, @NotNull GutterEditorManager gutterEditorManager) {
    this.project = project;
    this.gutterEditorManager = gutterEditorManager;
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
        gutterEditorManager.editorCreated(editor, psiFile);
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
      gutterEditorManager.editorReleased(editor);
    }
  }
}
