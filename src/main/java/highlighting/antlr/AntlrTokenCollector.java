package highlighting.antlr;

import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;
import java.awt.*;
import java.util.List;
import org.antlr.v4.runtime.*;

// TODO Phase III — AntlrTokenCollector (token-based syntax highlighting).

// This highlighter uses the ANTLR-generated MiniJavaLexer to turn the input text into a token
// stream. {@code collectMatches(String)} is the only method you need to implement: extract tokens
// of interest and map them to {@code HighlightRegions} using the colours from {@code
// MiniJavaColours}. Sorting, filtering of invalid regions, and conflict handling are performed by
// the base class {@code SyntaxHighlighter} via the template method {@code computeRegions(...)}.
public class AntlrTokenCollector extends SyntaxHighlighter {

    // TODO (Phase III — implement this method): Use the token stream produced by the
    // ANTLR-generated
    // {@code MiniJavaLexer} to collect highlight regions.
    //
    // Requirements / hints:
    // - Iterate over the lexer tokens (typically via {@code CommonTokenStream}); ignore the EOF
    // token.
    // - For each token type that should be coloured (e.g., keywords, string/char literals,
    // comments),
    // create a {@code HighlightRegion} with the corresponding colour from {@code MiniJavaColours}.
    // - Use {@code Token#getStartIndex()} and {@code Token#getStopIndex()} (inclusive) to compute
    // {@code [start, end)} ranges: {@code start = startIndex, end = stopIndex + 1}.
    // - Do not sort, merge, or resolve overlaps here; return all candidates as you find them.
    // Normalisation and conflict resolution are handled later by the template method.
    // - Annotation highlighting: colour '@' and the immediately following IDENTIFIER token (if
    // present).
    @Override
    public List<HighlightRegion> collectMatches(String text) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
