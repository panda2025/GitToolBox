package zielu.intellij.ui.gutter;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GutterAnnotationProvider {
  @Nullable
  GutterAnnotation annotate(@NotNull PsiFile psiFile, @NotNull Editor editor);
}
