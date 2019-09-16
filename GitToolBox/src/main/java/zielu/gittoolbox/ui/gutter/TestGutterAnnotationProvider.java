package zielu.gittoolbox.ui.gutter;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zielu.gittoolbox.cache.VirtualFileRepoCache;
import zielu.intellij.ui.gutter.GutterAnnotation;
import zielu.intellij.ui.gutter.GutterAnnotationProvider;

public class TestGutterAnnotationProvider implements GutterAnnotationProvider {
  private final VirtualFileRepoCache fileRepoCache;

  public TestGutterAnnotationProvider(@NotNull VirtualFileRepoCache fileRepoCache) {
    this.fileRepoCache = fileRepoCache;
  }

  @Nullable
  @Override
  public GutterAnnotation annotate(@NotNull PsiFile psiFile, @NotNull Editor editor) {
    if (psiFile.isPhysical()) {
      VirtualFile virtualFile = psiFile.getVirtualFile();
      if (virtualFile != null && fileRepoCache.isUnderGitRoot(virtualFile)) {
        TestGutterAnnotation annotation = new TestGutterAnnotation(editor, virtualFile);
        annotation.refresh();
        return annotation;
      }
    }
    return null;
  }
}
