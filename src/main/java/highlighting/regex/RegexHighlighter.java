package highlighting.regex;

import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;
import highlighting.presets.MiniJavaTokens;
import java.util.LinkedList;
import java.util.List;

// strategy applies each token independently to the entire input text and collects all resulting
// {@code HighlightRegion}s, even if they overlap. Conflicts are resolved in a separate step.

// collectMatches}, and override {@code resolveConflicts} to handle overlapping regions produced by
// the naive regex-based strategy.
public class RegexHighlighter extends SyntaxHighlighter {

    // {@code HighlightRegion}s, and combine all of these regions into a single list.
    @Override
    public List<HighlightRegion> collectMatches(String text) {
        List<HighlightRegion> highlightRegions = new LinkedList<>();
        for (Token token : MiniJavaTokens.defaultTokens()) {
            highlightRegions.addAll(token.test(text));
        }
        return highlightRegions;
    }

    // sorted.
    // For any overlapping regions, keep the one that appears first in this list (which reflects the
    // token order) and discard all later overlapping regions. Longer regions that start at the same
    // position are preferred because of the sorting in {@code normalize}.
    @Override
    public List<HighlightRegion> resolveConflicts(List<HighlightRegion> regions) {
        List<HighlightRegion> highlightRegions = new LinkedList<>();

        for (HighlightRegion region : regions) {
            int start = region.start();
            int end = region.end();
            boolean overlaps = false;

            for (HighlightRegion existing : highlightRegions) {
                int existingStart = existing.start();
                int existingEnd = existing.end();

                if (start < existingEnd && end > existingStart) {
                    overlaps = true;
                    break;
                }
            }

            if (!overlaps) {
                highlightRegions.add(region);
            }
        }
        return highlightRegions;
    }
}
