package highlighting.presets;

import highlighting.regex.Token;
import java.util.List;
import java.util.regex.Pattern;

public final class MiniJavaTokens {

    // regular expression to a colour (and, if applicable, a specific matching group). The order of
    // tokens in this list determines their relative priority during highlighting. One example token
    // definition is provided below; define the remaining tokens in an analogous way.

    // Basic token set for MiniJava. Extend this list with further tokens as needed (e.g.
    // identifiers,
    // numeric literals, operators, brackets, whitespace), following the same pattern. Each token is
    // defined by a regular expression and a colour. Optionally, a specific capturing group within
    // the
    // pattern can be selected as the "highlighted" region.
    public static List<Token> defaultTokens() {
        return List.of(
            // Example: string literals (students should define further tokens below)
            Token.of(
                Pattern.compile("\"([^\"\\\\]|\\\\.)*\""),
                MiniJavaColours.STRING_LITERAL_COLOUR),

            // annotations, comments, identifiers, numbers, operators, etc.
            Token.of(
                Pattern.compile("\"([^\"\\\\]|\\\\.)*\""),
                MiniJavaColours.STRING_LITERAL_COLOUR),

            // Character literals
            Token.of(
                Pattern.compile("'([^'\\\\]|\\\\.)'"), MiniJavaColours.CHAR_LITERAL_COLOUR),

            // Keywords
            Token.of(
                Pattern.compile(
                    "\\b(class|public|private|protected|static|final|void|int|boolean|char|if|else|while|for|return|new|this|null|true|false|extends|implements|interface|abstract|try|catch|finally|throw|throws|switch|case|break|continue|default)\\b"),
                MiniJavaColours.KEYWORD_COLOUR),

            // Annotations
            Token.of(
                Pattern.compile("@[A-Za-z_][A-Za-z0-9_]*"),
                MiniJavaColours.ANNOTATION_COLOUR),

            // JavaDoc comments
            Token.of(
                Pattern.compile("/\\*\\*([\\s\\S]*?)\\*/"),
                MiniJavaColours.JAVADOC_COMMENT_COLOUR),

            // Block comments
            Token.of(
                Pattern.compile("/\\*([\\s\\S]*?)\\*/"),
                MiniJavaColours.BLOCK_COMMENT_COLOUR),

            // Line comments
            Token.of(Pattern.compile("//.*"), MiniJavaColours.LINE_COMMENT_COLOUR));
    }
}
