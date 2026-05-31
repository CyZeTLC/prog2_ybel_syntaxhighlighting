import static org.junit.jupiter.api.Assertions.*;

import highlighting.core.HighlightRegion;
import highlighting.presets.MiniJavaColours;
import highlighting.regex.RegexHighlighter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegexHighlighterTest {

    private RegexHighlighter highlighter;

    @BeforeEach
    void setUp() {
        highlighter = new RegexHighlighter();
    }

    @Test
    void testEmptyTextAndNoMatches() {
        String emptyText = "";
        List<HighlightRegion> matchesEmpty = highlighter.collectMatches(emptyText);
        List<HighlightRegion> resolvedEmpty = highlighter.resolveConflicts(matchesEmpty);
        assertTrue(resolvedEmpty.isEmpty());

        String plainText = "variable = 42;";
        List<HighlightRegion> matchesPlain = highlighter.collectMatches(plainText);
        List<HighlightRegion> resolvedPlain = highlighter.resolveConflicts(matchesPlain);
        assertTrue(resolvedPlain.isEmpty());
    }

    @Test
    void testSimpleCasesWithoutOverlaps() {
        String text = "public @Override \"hello\"";
        List<HighlightRegion> matches = highlighter.collectMatches(text);
        List<HighlightRegion> resolved = highlighter.resolveConflicts(matches);

        assertEquals(3, resolved.size());
    }

    @Test
    void testConsecutiveRegions() {
        List<HighlightRegion> regions = new ArrayList<>();
        regions.add(new HighlightRegion(0, 5, MiniJavaColours.KEYWORD_COLOUR));
        regions.add(new HighlightRegion(5, 10, MiniJavaColours.KEYWORD_COLOUR));

        List<HighlightRegion> resolved = highlighter.resolveConflicts(regions);

        assertEquals(2, resolved.size());
        assertEquals(0, resolved.get(0).start());
        assertEquals(5, resolved.get(0).end());
        assertEquals(5, resolved.get(1).start());
        assertEquals(10, resolved.get(1).end());
    }

    @Test
    void testKeywordInsideComment() {
        String text = "// class Test";
        List<HighlightRegion> matches = highlighter.collectMatches(text);

        boolean hasComment = false;
        boolean hasKeyword = false;

        for (HighlightRegion region : matches) {
            if (region.colour().equals(MiniJavaColours.LINE_COMMENT_COLOUR)) {
                hasComment = true;
            }
            if (region.colour().equals(MiniJavaColours.KEYWORD_COLOUR)) {
                hasKeyword = true;
            }
        }

        assertTrue(hasComment);
        assertTrue(hasKeyword);

        List<HighlightRegion> resolved = highlighter.resolveConflicts(matches);

        assertEquals(1, resolved.size());
        assertEquals(MiniJavaColours.LINE_COMMENT_COLOUR, resolved.get(0).colour());
    }

    @Test
    void testJavadocAndBlockCommentOverlap() {
        String text = "/** javadoc */";
        List<HighlightRegion> matches = highlighter.collectMatches(text);

        boolean hasJavadoc = false;
        boolean hasBlockComment = false;

        for (HighlightRegion region : matches) {
            if (region.colour().equals(MiniJavaColours.JAVADOC_COMMENT_COLOUR)) {
                hasJavadoc = true;
            }
            if (region.colour().equals(MiniJavaColours.BLOCK_COMMENT_COLOUR)) {
                hasBlockComment = true;
            }
        }

        assertTrue(hasJavadoc);
        assertTrue(hasBlockComment);

        List<HighlightRegion> resolved = highlighter.resolveConflicts(matches);

        assertEquals(1, resolved.size());
        assertEquals(MiniJavaColours.JAVADOC_COMMENT_COLOUR, resolved.get(0).colour());
    }

    @Test
    void testTokenPriorityInResolveConflicts() {
        List<HighlightRegion> regions = new ArrayList<>();
        HighlightRegion highPriority =
                new HighlightRegion(0, 10, MiniJavaColours.STRING_LITERAL_COLOUR);
        HighlightRegion lowPriority = new HighlightRegion(2, 8, MiniJavaColours.KEYWORD_COLOUR);

        regions.add(highPriority);
        regions.add(lowPriority);

        List<HighlightRegion> resolved = highlighter.resolveConflicts(regions);

        assertEquals(1, resolved.size());
        assertEquals(MiniJavaColours.STRING_LITERAL_COLOUR, resolved.get(0).colour());
    }
}
