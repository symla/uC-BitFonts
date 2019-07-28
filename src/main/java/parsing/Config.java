package parsing;

import java.util.Objects;


/**
 * Font config. Currently only contains width and height of chars.
 */
public class Config {

    /**
     * Width of each char.
     */
    private final int charWidth;

    /**
     * Height of each char.
     */
    private final int charHeight;

    public Config(int charWidth, int charHeight) {
        if ( charWidth < 1 ) throw new IllegalArgumentException("Minimum char width is 1, but got "+charWidth+".");
        if ( charHeight < 5) throw new IllegalArgumentException("Minimum char height is 5, but got "+charHeight+".");
        this.charWidth = charWidth;
        this.charHeight = charHeight;
    }

    public int getCharWidth() {
        return charWidth;
    }

    public int getCharHeight() {
        return charHeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return getCharWidth() == config.getCharWidth() &&
                getCharHeight() == config.getCharHeight();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCharWidth(), getCharHeight());
    }

    @Override
    public String toString() {
        return "Config{" +
                "charWidth=" + charWidth +
                ", charHeight=" + charHeight +
                '}';
    }
}
