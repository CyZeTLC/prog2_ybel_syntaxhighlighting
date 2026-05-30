package highlighting;

import highlighting.antlr.*;
import highlighting.core.SyntaxHighlighter;
import highlighting.presets.Texts;
import highlighting.regex.*;
import highlighting.ui.EditorUI;

public class Main {

    public static void main(String... args) {
        // Phase I: RegexHighlighter
        SyntaxHighlighter regex = new RegexHighlighter();

        // Phase II: ScanningHighlighter
        SyntaxHighlighter scanning = new ScanningHighlighter();

        // Phase III: AntlrTokenCollector (tokenbasiert)
        SyntaxHighlighter antlrToken = new AntlrTokenCollector();

        // and go ...
        EditorUI.show(Texts.START_TEXT, regex);
        EditorUI.show(Texts.START_TEXT, scanning);
        // EditorUI.show(Texts.START_TEXT, antlrToken);
    }
}
