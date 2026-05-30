package highlighting.ui;

import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;

public final class EditorUI {

    private final JFrame frame;
    private final JTextPane editorPane;
    private final SyntaxHighlighter highlighter;

    private final StyledDocument doc;
    private final Map<Color, Style> styleCache = new HashMap<>();

    private final JLabel messageLabel;

    private File currentFile;

    public static void show(String defaultText, SyntaxHighlighter highlighter) {
        SwingUtilities.invokeLater(() -> new EditorUI(defaultText, highlighter));
    }

    private EditorUI(String defaultText, SyntaxHighlighter highlighter) {
        this.highlighter = highlighter;

        this.editorPane =
                new JTextPane() {
                    @Override
                    public boolean getScrollableTracksViewportWidth() {
                        return false; // do not do soft breaks
                    }
                };
        this.editorPane.setText(defaultText);
        this.editorPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        this.doc = editorPane.getStyledDocument();

        this.frame = new JFrame("Syntax-Highlighting-Editor");
        this.messageLabel = new JLabel();

        initFrameLayout();
        initDocumentListener();

        setStatus("Bereit");

        scheduleHighlighting();
    }

    private void initFrameLayout() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setJMenuBar(createMenuBar());

        var scrollPane =
                new JScrollPane(
                        editorPane,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        var lineNumbers = new LineNumberView(editorPane);
        scrollPane.setRowHeaderView(lineNumbers);
        frame.add(scrollPane, BorderLayout.CENTER);

        var statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        statusPanel.add(messageLabel, BorderLayout.WEST);
        frame.add(statusPanel, BorderLayout.SOUTH);

        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void initDocumentListener() {
        doc.addDocumentListener(
                new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        scheduleHighlighting();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        scheduleHighlighting();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        // ignore: this is just for attribute changes
                    }
                });
    }

    private JMenuBar createMenuBar() {
        var menuBar = new JMenuBar();
        var fileMenu = new JMenu("Datei");

        var openItem = new JMenuItem("Öffnen...");
        openItem.addActionListener(e -> openFile());

        var saveItem = new JMenuItem("Speichern");
        saveItem.addActionListener(e -> saveFile(false));

        var saveAsItem = new JMenuItem("Speichern unter...");
        saveAsItem.addActionListener(e -> saveFile(true));

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);

        menuBar.add(fileMenu);

        return menuBar;
    }

    private void openFile() {
        var chooser = createFileChooser();
        var result = chooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            var file = chooser.getSelectedFile();
            try {
                var content = Files.readString(file.toPath());
                editorPane.setText(content);
                this.currentFile = file;
                frame.setTitle("Syntax-Highlighting-Editor - " + file.getName());
                setStatus("Datei geöffnet: " + file.getAbsolutePath());
                scheduleHighlighting();
            } catch (IOException ex) {
                setStatus("Fehler beim Öffnen: " + ex.getMessage());
            }
        } else setStatus("Öffnen abgebrochen");
    }

    private void saveFile(boolean saveAs) {
        try {
            if (currentFile == null || saveAs) {
                var chooser = createFileChooser();
                var result = chooser.showSaveDialog(frame);
                if (result != JFileChooser.APPROVE_OPTION) {
                    setStatus("Speichern abgebrochen");
                    return;
                }
                currentFile = chooser.getSelectedFile();
            }

            var content = doc.getText(0, doc.getLength());
            Files.writeString(currentFile.toPath(), content);
            frame.setTitle("Syntax-Highlighting-Editor - " + currentFile.getName());
            setStatus("Gespeichert: " + currentFile.getAbsolutePath());
        } catch (Exception ex) {
            setStatus("Fehler beim Speichern: " + ex.getMessage());
        }
    }

    private JFileChooser createFileChooser() {
        var chooser = new JFileChooser();
        chooser.setDialogTitle("Datei wählen");
        chooser.setAcceptAllFileFilterUsed(true);
        chooser.addChoosableFileFilter(
                new FileNameExtensionFilter("Java-Dateien (*.java)", "java"));
        return chooser;
    }

    private void setStatus(String message) {
        messageLabel.setText(message);
    }

    private void scheduleHighlighting() {
        // needs to be asynchronous to avoid "Attempt to mutate in notification" exceptions
        SwingUtilities.invokeLater(this::updateHighlighting);
    }

    private void updateHighlighting() {
        String text;
        try {
            text = doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            setStatus("Fehler beim Lesen des Dokuments: " + e.getMessage());
            return;
        }

        var length = text.length();
        if (length == 0) return;

        Style defaultStyle = styleCache.computeIfAbsent(Color.BLACK, this::computeStyleForColor);
        doc.setCharacterAttributes(0, length, defaultStyle, true);

        List<HighlightRegion> regions;
        try {
            regions = highlighter.computeRegions(text);
            setStatus("Bereit");
        } catch (Exception ex) {
            var msg = ex.getMessage();
            if (msg == null || msg.isBlank()) msg = "Unbekannter Fehler beim Highlighting";
            setStatus(msg);
            return;
        }

        regions.forEach(
                r -> {
                    var start = r.start();
                    var end = r.end();
                    var len = end - start;
                    if (start < 0 || end > length || len <= 0) return;
                    var style = styleCache.computeIfAbsent(r.colour(), this::computeStyleForColor);
                    doc.setCharacterAttributes(start, len, style, true);
                });
    }

    private Style computeStyleForColor(Color colour) {
        var style = editorPane.addStyle("colour-" + colour.getRGB(), null);
        StyleConstants.setForeground(style, colour);
        StyleConstants.setBold(style, false);
        StyleConstants.setItalic(style, false);
        return style;
    }
}
