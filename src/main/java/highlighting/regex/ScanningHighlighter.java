package highlighting.regex;

import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;
import java.util.*;

// TODO: Implement a scanning-based highlighting strategy that reads the input from left to right.
// At each position, select the longest token that matches at this position. If there is a tie, the
// token that appears earlier in the token list should be preferred.

// TODO: Make this class inherit from {@code SyntaxHighlighter} and implement the abstract method
// {@code collectMatches}. The scanning algorithm should ensure that the resulting list of regions
// is already sorted, non-overlapping and contains only valid regions, so that no additional
// normalisation or conflict resolution is required. Therefore, {@code resolveConflicts} can be left
// as is, and {@code normalize} should be overridden as the identity function.
public class ScanningHighlighter extends SyntaxHighlighter {

    // TODO: Implement the scanning-based matching strategy here. Iterate from left to right over
    // the
    // input, determine the best matching token at each position, and collect all resulting
    // highlight
    // regions in order.
    @Override
    public List<HighlightRegion> collectMatches(String text) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    // TODO: Implement the identity function here.
    @Override
    public List<HighlightRegion> normalize(List<HighlightRegion> candidates) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
