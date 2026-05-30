package highlighting.core;

import highlighting.regex.RegexHighlighter;
import highlighting.regex.ScanningHighlighter;
import highlighting.ui.EditorUI;
import java.util.List;

public abstract class SyntaxHighlighter {

    /**
     * Template method: will be called in {@link EditorUI}.
     *
     * @param text input text to be highlighted
     * @return a sorted list of highlight regions
     */
    public final List<HighlightRegion> computeRegions(String text) {
        var candidates = collectMatches(text);
        var normalized = normalize(candidates);
        var resolved = resolveConflicts(normalized);
        return resolved;
    }

    /**
     * Abstract method that must be implemented by concrete subclasses.
     *
     * <p>Applies the tokens defined in {@code tokens} to the text and collects all matching {@code
     * HighlightRegion}s.
     *
     * <p>This method must be implemented by concrete subclasses (e.g. naïve application of all
     * regular expressions (see {@link RegexHighlighter}) or scanning from left to right (see {@link
     * ScanningHighlighter}).
     *
     * @param text the input text to analyse for highlight regions
     * @return list of all highlight regions found in {@code text}
     */
    public abstract List<HighlightRegion> collectMatches(String text);

    /**
     * Hook 1: remove invalid regions and sort in ascending order.
     *
     * @param candidates the candidate highlight regions
     * @return a filtered and sorted list of highlight regions
     */
    public List<HighlightRegion> normalize(List<HighlightRegion> candidates) {
        return candidates.stream()
                .filter(r -> r.start() < r.end())
                .sorted(
                        (a, b) -> {
                            int c = Integer.compare(a.start(), b.start());
                            return (c != 0) ? c : Integer.compare(b.end(), a.end());
                        })
                .toList();
    }

    /**
     * Hook 2: resolve conflicts. Default: "keep all".
     *
     * @param normalized the normalised list of highlight regions
     * @return the list of highlight regions after conflict resolution
     */
    public List<HighlightRegion> resolveConflicts(List<HighlightRegion> normalized) {
        return normalized;
    }
}
