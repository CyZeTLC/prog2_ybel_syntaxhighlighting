package highlighting.core;

import java.awt.Color;
import java.util.Objects;

/** Represents a highlighted region within the text together with its colour. */
public final class HighlightRegion {
    private final int start;
    private final int end;
    private final Color colour;

    /**
     * @param start the start offset of the highlighted region (inclusive)
     * @param end the end offset of the highlighted region (exclusive)
     * @param colour the colour used to highlight this region
     */
    public HighlightRegion(int start, int end, Color colour) {
        this.start = start;
        this.end = end;
        this.colour = colour;
    }

    public int start() {
        return start;
    }

    public int end() {
        return end;
    }

    public Color colour() {
        return colour;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (HighlightRegion) obj;
        return this.start == that.start
                && this.end == that.end
                && Objects.equals(this.colour, that.colour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, colour);
    }
}
