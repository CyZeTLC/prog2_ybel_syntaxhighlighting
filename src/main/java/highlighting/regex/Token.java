package highlighting.regex;

import highlighting.core.HighlightRegion;
import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Represents a lexical token defined by a regular expression.
 *
 * <p>The class encapsulates a precompiled regular expression ({@link Pattern}) to be applied to
 * input text in the {@link Token#test(String)} method. Each token has an associated colour used to
 * highlight the matched text. You can also specify the capturing group to be used for highlighting
 * – this is usually 0 (i.e. the entire match will be highlighted), but can be set to a different
 * value when the regular expression contains groups and only a specific group should be
 * highlighted.
 */
public final class Token {
    private final Pattern pattern;
    private final int matchingGroup;
    private final Color colour;

    private Token(Pattern pattern, int matchingGroup, Color colour) {
        this.pattern = pattern;
        this.matchingGroup = matchingGroup;
        this.colour = colour;
    }

    /**
     * Creates a new token.
     *
     * @param pattern the precompiled regular expression applied to the text in {@link
     *     Token#test(String)}
     * @param matchingGroup the capturing group to be used for highlighting
     * @param colour the colour used for highlighting for this token
     * @return a new {@code Token} instance
     */
    public static Token of(Pattern pattern, int matchingGroup, Color colour) {
        return new Token(pattern, matchingGroup, colour);
    }

    /**
     * Creates a new token that uses the entire match for highlighting.
     *
     * @param pattern the precompiled regular expression applied to the text in {@link
     *     Token#test(String)}
     * @param colour the colour used for highlighting for this token
     * @return a new {@code Token} instance
     */
    public static Token of(Pattern pattern, Color colour) {
        return new Token(pattern, 0, colour);
    }

    /**
     * Applies this token (its regular expression) to the given input string.
     *
     * @param s the input string to which this pattern is applied
     * @return a list of all matches found using the pattern
     */
    public List<HighlightRegion> test(String s) {
        return pattern.matcher(s).results().map(this::toRegion).collect(Collectors.toList());
    }

    /**
     * Converts a match result into a {@link HighlightRegion} using the configured matching group
     * and colour.
     */
    private HighlightRegion toRegion(MatchResult mr) {
        return new HighlightRegion(mr.start(matchingGroup), mr.end(matchingGroup), colour);
    }

    public Pattern pattern() {
        return pattern;
    }

    public int matchingGroup() {
        return matchingGroup;
    }

    public Color colour() {
        return colour;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Token) obj;
        return Objects.equals(this.pattern, that.pattern)
                && this.matchingGroup == that.matchingGroup
                && Objects.equals(this.colour, that.colour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern, matchingGroup, colour);
    }
}
