import static org.junit.jupiter.api.Assertions.*;

import highlighting.presets.MiniJavaTokens;
import highlighting.regex.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MiniJavaTokensValidationTest {

    private Pattern keywordPattern;
    private Pattern annotationPattern;
    private Pattern stringPattern;
    private Pattern lineCommentPattern;

    @BeforeEach
    void setUp() {
        List<Token> tokens = MiniJavaTokens.defaultTokens();
        stringPattern = tokens.get(0).pattern();
        keywordPattern = tokens.get(3).pattern();
        annotationPattern = tokens.get(4).pattern();
        lineCommentPattern = tokens.get(7).pattern();
    }

    private List<Integer> getMatchIndices(Pattern pattern, String text) {
        List<Integer> indices = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            indices.add(matcher.start());
        }
        return indices;
    }

    @Test
    void testMatchesAtDifferentPositions() {
        String atStart = "public class Test {}";
        List<Integer> indicesStart = getMatchIndices(keywordPattern, atStart);
        assertTrue(indicesStart.contains(0));

        String inMiddle = "final public static void main";
        List<Integer> indicesMiddle = getMatchIndices(keywordPattern, inMiddle);
        assertTrue(indicesMiddle.contains(13));

        String atEnd = "if (isValid) return";
        List<Integer> indicesEnd = getMatchIndices(keywordPattern, atEnd);
        assertTrue(indicesEnd.contains(13));
    }

    @Test
    void testMultipleMatchesInSameText() {
        String textKeywords = "public static final int value = 0;";
        List<Integer> indicesKeywords = getMatchIndices(keywordPattern, textKeywords);
        assertEquals(4, indicesKeywords.size());

        String textStrings = "\"first\" + \"second\" + \"third\"";
        List<Integer> indicesStrings = getMatchIndices(stringPattern, textStrings);
        assertEquals(3, indicesStrings.size());
    }

    @Test
    void testNoMatch() {
        String text = "variable_name = 12345;";
        assertTrue(getMatchIndices(keywordPattern, text).isEmpty());
        assertTrue(getMatchIndices(stringPattern, text).isEmpty());
        assertTrue(getMatchIndices(annotationPattern, text).isEmpty());
    }

    @Test
    void testCommentContainsKeywordLikeText() {
        String text = "// this contains public and return keywords";
        Matcher matcher = lineCommentPattern.matcher(text);
        assertTrue(matcher.matches());
    }

    @Test
    void testAnnotationAtStartAndWithSpaces() {
        String atStart = "@Override";
        List<Integer> indicesStart = getMatchIndices(annotationPattern, atStart);
        assertEquals(1, indicesStart.size());
        assertEquals(0, indicesStart.get(0));

        String withSpaces = "    @Test";
        List<Integer> indicesSpaces = getMatchIndices(annotationPattern, withSpaces);
        assertEquals(1, indicesSpaces.size());
        assertEquals(4, indicesSpaces.get(0));
    }

    @Test
    void testStringsWithCommentSymbols() {
        String withLineComment = "String url = \"https://google.com\";";
        Matcher matcherLine = stringPattern.matcher(withLineComment);
        assertTrue(matcherLine.find());
        assertEquals("\"https://google.com\"", matcherLine.group());

        String withBlockComment = "String text = \"Text /* with comment */ inside\";";
        Matcher matcherBlock = stringPattern.matcher(withBlockComment);
        assertTrue(matcherBlock.find());
        assertEquals("\"Text /* with comment */ inside\"", matcherBlock.group());
    }
}
