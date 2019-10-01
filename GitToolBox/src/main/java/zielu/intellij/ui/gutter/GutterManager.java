package zielu.intellij.ui.gutter;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.messages.MessageBusConnection;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import zielu.intellij.ui.ZUiUtil;

public abstract class GutterManager implements BaseComponent {
  private static final Key<EditorFactoryListener> FACTORY_LISTENER_KEY = Key.create(GutterManager.class.getName() + ".key");
  private final Project project;
  private final GutterEditorManager gutterEditorManager;

  public GutterManager(@NotNull Project project, @NotNull Function<Project, GutterAnnotationProvider> providerLocator) {
    this.project = project;
    this.gutterEditorManager = new GutterEditorManager(project, providerLocator);
  }

  private boolean isCurrentProject(Project project) {
    return this.project == project;
  }

  @Override
  public final void initComponent() {
    MessageBusConnection connection = project.getMessageBus()
                                          .connect(project);
    connection.subscribe(ProjectManager.TOPIC, new ProjectManagerListener() {
      @Override
      public void projectOpened(@NotNull Project currentProject) {
        if (isCurrentProject(currentProject)) {
          EditorFactoryListener listener = new GutterEditorFactoryListener(currentProject, gutterEditorManager);
          EditorFactory.getInstance()
              .addEditorFactoryListener(listener, currentProject);
          currentProject.putUserData(FACTORY_LISTENER_KEY, listener);
          renewGutters();
        }
      }
    });
  }

  protected void renewGutters() {
    ZUiUtil.invokeLater(project, this::renewGutterImpl);
  }

  private void renewGutterImpl() {
    FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
    VirtualFile[] openFiles = fileEditorManager.getOpenFiles();
    for (VirtualFile openFile : openFiles) {
      FileEditor[] allEditors = fileEditorManager.getAllEditors(openFile);
      renewGutter(allEditors, openFile);
    }
  }

  private void renewGutter(@NotNull FileEditor[] fileEditors, @NotNull VirtualFile file) {
    PsiFile psiFile = ReadAction.compute(() -> {
      if (project.isDisposed()) return null;
      return PsiManager.getInstance(project).findFile(file);
    });
    if (psiFile != null && psiFile.isValid()) {
      for (FileEditor fileEditor : fileEditors) {
        if (fileEditor instanceof TextEditor) {
          Editor editor = ((TextEditor) fileEditor).getEditor();
          gutterEditorManager.renewEditor(editor, psiFile);
        }
      }
    }
  }
}
