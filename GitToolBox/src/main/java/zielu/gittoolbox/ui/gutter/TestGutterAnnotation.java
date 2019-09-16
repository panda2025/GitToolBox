package zielu.gittoolbox.ui.gutter;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.markup.ActiveGutterRenderer;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.LineMarkerRendererEx;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zielu.intellij.ui.gutter.GutterAnnotation;

public class TestGutterAnnotation implements GutterAnnotation {
  private final Editor editor;
  private final VirtualFile file;

  public TestGutterAnnotation(@NotNull Editor editor, @NotNull VirtualFile file) {
    this.editor = editor;
    this.file = file;
  }

  public void refresh() {
    Document document = editor.getDocument();
    MarkupModel markupModel = DocumentMarkupModel.forDocument(document, editor.getProject(), true);
    for (int line = 0, count = document.getLineCount(); line < count; line++) {
      int startOffset = document.getLineStartOffset(line);
      int endOffset = document.getLineEndOffset(line);
      /*RangeHighlighter highlighter =
          markupModel.addRangeHighlighter(startOffset, endOffset, HighlighterLayer.SELECTION - 1, null,
              HighlighterTargetArea.LINES_IN_RANGE);*/
      RangeHighlighter highlighter1 =
          markupModel.addLineHighlighter(line, HighlighterLayer.SELECTION - 1, null);
      highlighter1.setLineMarkerRenderer(new MyMarkerRenderer(JBColor.PINK));
      RangeHighlighter highlighter2 =
          markupModel.addLineHighlighter(line, HighlighterLayer.SELECTION - 1, null);
      highlighter2.setLineMarkerRenderer(new MyMarkerRenderer(JBColor.YELLOW));
    }
  }

  private class MyMarkerRenderer implements ActiveGutterRenderer, LineMarkerRendererEx {
    private final JBColor color;

    private MyMarkerRenderer(JBColor color) {
      this.color = color;
    }

    @Override
    public void paint(Editor editor, Graphics g, Rectangle r) {
      g.setColor(color);
      g.fillRect(r.x, r.y, r.width, r.height);
    }

    @Nullable
    @Override
    public String getTooltipText() {
      return null;
    }

    @Override
    public void doAction(@NotNull Editor editor, @NotNull MouseEvent e) {

    }

    @Override
    public boolean canDoAction(@NotNull Editor editor, @NotNull MouseEvent e) {
      return false;
    }

    @Override
    public boolean canDoAction(@NotNull MouseEvent e) {
      return false;
    }

    @NotNull
    @Override
    public String getAccessibleName() {
      return null;
    }

    @Nullable
    @Override
    public String getAccessibleTooltipText() {
      return null;
    }

    @Nullable
    @Override
    public Rectangle calcBounds(@NotNull Editor editor, int lineNum, @NotNull Rectangle preferredBounds) {
      return preferredBounds;
    }

    @NotNull
    @Override
    public Position getPosition() {
      return Position.RIGHT;
    }
  }

  @Override
  public void dispose() {

  }
}
