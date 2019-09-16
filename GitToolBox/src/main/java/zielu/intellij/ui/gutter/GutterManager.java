package zielu.intellij.ui.gutter;

import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.util.messages.MessageBusConnection;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public abstract class GutterManager implements BaseComponent {
  private final Project project;
  private final Function<Project, GutterAnnotationProvider> providerLocator;

  public GutterManager(@NotNull Project project, @NotNull Function<Project, GutterAnnotationProvider> providerLocator) {
    this.project = project;
    this.providerLocator = providerLocator;
  }

  @Override
  public void initComponent() {
    MessageBusConnection connection = project.getMessageBus().connect(project);
    connection.subscribe(ProjectManager.TOPIC, new ProjectManagerListener() {
      @Override
      public void projectOpened(@NotNull Project project) {
        GutterAnnotationProvider provider = providerLocator.apply(project);
        EditorFactory.getInstance().addEditorFactoryListener(new GutterEditorFactoryListener(project, provider), project);
      }
    });
  }
}
