package highlighting.ui;

import java.awt.*;
import java.util.stream.IntStream;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

/** Simple component for displaying line numbers to the left of a {@link JTextComponent}. */
class LineNumberView extends JComponent {
    private static final int MARGIN = 5;

    private final JTextComponent textComponent;
    private final FontMetrics fontMetrics;

    LineNumberView(JTextComponent textComponent) {
        this.textComponent = textComponent;
        this.fontMetrics = textComponent.getFontMetrics(textComponent.getFont());
        setPreferredWidth();
        setupChangeHandling();
        setupResizeHandling();
    }

    private void setPreferredWidth() {
        var root = textComponent.getDocument().getDefaultRootElement();
        var lineCount = root.getElementCount();
        var digits = Math.max(2, String.valueOf(lineCount).length());
        var width = MARGIN * 2 + fontMetrics.charWidth('0') * digits;
        var dim = new Dimension(width, Integer.MAX_VALUE);
        setPreferredSize(dim);
        setSize(dim);
    }

    private void setupChangeHandling() {
        // repaint line numbers on document changes
        this.textComponent
                .getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            @Override
                            public void insertUpdate(DocumentEvent e) {
                                repaint();
                                setPreferredWidth();
                            }

                            @Override
                            public void removeUpdate(DocumentEvent e) {
                                repaint();
                                setPreferredWidth();
                            }

                            @Override
                            public void changedUpdate(DocumentEvent e) {
                                // ignore: this is just for attribute changes
                            }
                        });
    }

    private void setupResizeHandling() {
        // repaint line numbers on size changes
        this.textComponent.addComponentListener(
                new java.awt.event.ComponentAdapter() {
                    @Override
                    public void componentResized(java.awt.event.ComponentEvent e) {
                        repaint();
                    }
                });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        var clip = g.getClipBounds();
        var startOffset = textComponent.viewToModel2D(new Point(0, clip.y));
        var endOffset = textComponent.viewToModel2D(new Point(0, clip.y + clip.height));

        var root = textComponent.getDocument().getDefaultRootElement();
        var startLine = root.getElementIndex(startOffset);
        var endLine = root.getElementIndex(endOffset);

        IntStream.rangeClosed(startLine, endLine)
                .forEach(
                        line -> {
                            var lineElem = root.getElement(line);
                            try {
                                var r =
                                        textComponent
                                                .modelToView2D(lineElem.getStartOffset())
                                                .getBounds();
                                var lineNumber = Integer.toString(line + 1);
                                var x = getWidth() - MARGIN - fontMetrics.stringWidth(lineNumber);
                                var y = r.y + r.height - fontMetrics.getDescent();
                                g.drawString(lineNumber, x, y);
                            } catch (BadLocationException e) {
                                // ignored as we do not handle external input here
                            }
                        });
    }
}
