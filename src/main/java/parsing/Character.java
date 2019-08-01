package parsing;

import java.util.Arrays;
import java.util.Objects;

public class Character {

    /**
     * Redundant metadata, can be retrieved from font config.
     */
    private final int width;

    /**
     * Redundant metadata, can be retrieved from font config.
     */
    private final int height;

    /**
     * Preserve flag if char is empty.
     */
    private final boolean preserve;

    /**
     * Flag if character is empty.
     */
    private final boolean empty;

    /**
     * Amount of pixels to move char to top. If > 0, offsetBottom must be zero.
     */
    private final int offsetTop;

    /**
     * Amount of pixels to move char to bottom. If > 0, offsetTop must be zero.
     */
    private final int offsetBottom;

    /**
     * Array of pixels. Length of array must be {@code charWidth * charHeight} of font config.
     * Must not be empty. If empty flag set, it must contain only false pixels.
     */
    private final boolean pixels[];

    /**
     * 2D Array of pixels (first index: x, second index: y).
     * Must not be empty. If empty flag set, it must contain only false pixels.
     */
    private final boolean pixels2D[][];

    public Character(final int width, final int height, boolean preserve, boolean empty, int offsetTop, int offsetBottom, boolean[] pixels, boolean[][] pixels2D) {
        if ( width < 1 ) throw new IllegalArgumentException("Minimum char width is 1, but got "+width+".");
        if ( height < 5) throw new IllegalArgumentException("Minimum char height is 5, but got "+height+".");

        if ( offsetTop != 0 && offsetBottom != 0 ) throw new IllegalArgumentException("OffsetTop was set " +
                "("+offsetTop+") and offsetBottom ("+offsetBottom+"), but only one can be set.");

        if ( pixels == null ) throw new NullPointerException("pixels must not be null.");
        if ( pixels2D == null ) throw new NullPointerException("pixels2D must not be null.");

        if ( pixels.length == 0 ) throw new IllegalArgumentException("pixels must not be empty.");
        if ( pixels2D.length == 0 ) throw new IllegalArgumentException("pixels2D must not be empty.");

        this.width = width;
        this.height = height;
        this.preserve = preserve;
        this.empty = empty;
        this.offsetTop = offsetTop;
        this.offsetBottom = offsetBottom;
        this.pixels = pixels;
        this.pixels2D = pixels2D;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isPreserve() {
        return preserve;
    }

    public boolean isEmpty() {
        return empty;
    }

    public int getOffsetTop() {
        return offsetTop;
    }

    public int getOffsetBottom() {
        return offsetBottom;
    }

    public boolean[] getPixels() {
        return pixels;
    }

    public boolean[][] getPixels2D() {
        return pixels2D;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Character character = (Character) o;
        return getWidth() == character.getWidth() &&
                getHeight() == character.getHeight() &&
                isPreserve() == character.isPreserve() &&
                isEmpty() == character.isEmpty() &&
                getOffsetTop() == character.getOffsetTop() &&
                getOffsetBottom() == character.getOffsetBottom() &&
                Arrays.equals(getPixels(), character.getPixels()) &&
                Arrays.equals(getPixels2D(), character.getPixels2D());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getWidth(), getHeight(), isPreserve(), isEmpty(), getOffsetTop(), getOffsetBottom());
        result = 31 * result + Arrays.hashCode(getPixels());
        result = 31 * result + Arrays.hashCode(getPixels2D());
        return result;
    }

    @Override
    public String toString() {
        return "Character{" +
                "width=" + width +
                ", height=" + height +
                ", preserve=" + preserve +
                ", empty=" + empty +
                ", offsetTop=" + offsetTop +
                ", offsetBottom=" + offsetBottom +
                ", pixels=" + Arrays.toString(pixels) +
                ", pixels2D=" + Arrays.toString(pixels2D) +
                '}';
    }

    /**
     * Prints character as "2d string".
     * @return Returns char as visual string.
     */
    public String pixels2DtoString() {
        final StringBuilder sb = new StringBuilder();
        final int width = this.pixels2D.length;
        final int height = this.pixels2D[0].length;

        for ( int y = 0; y < height; y++ ) {
            for ( int x = 0; x < width; x++ ) {
                sb.append((this.pixels2D[x][y] ? "#" : " "));
            }
            if ( y != height-1) sb.append("\n");
        }

        return sb.toString();
    }

    public static boolean[][] pixels_in_row_to_2d(final int width, final int height, final boolean pixels_in_row[]) {
        boolean result[][] = new boolean[width][height];

        for ( int i = 0; i < pixels_in_row.length; i++ ) {
            result[i % width][i/width] = pixels_in_row[i];
        }

        return result;
    }
}
