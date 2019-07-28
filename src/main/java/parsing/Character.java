package parsing;

import java.util.Arrays;
import java.util.Objects;

public class Character {

    /**
     * Preserve flag if char is empty.
     */
    private final boolean preserve;

    /**
     * Flag if character is empty.
     */
    private final boolean empty;

    /**
     * Array of pixels. Length of array must be {@code charWidth * charHeight} of font config.
     */
    private final boolean pixels[];

    public Character(boolean preserve, boolean empty, boolean pixels[]) {
        this.preserve = preserve;
        this.empty = empty;
        this.pixels = pixels;
    }

    public boolean isPreserve() {
        return preserve;
    }

    public boolean isEmpty() {
        return empty;
    }

    public boolean[] getPixels() {
        return pixels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Character character = (Character) o;
        return isPreserve() == character.isPreserve() &&
                isEmpty() == character.isEmpty() &&
                Arrays.equals(getPixels(), character.getPixels());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(isPreserve(), isEmpty());
        result = 31 * result + Arrays.hashCode(getPixels());
        return result;
    }

    @Override
    public String toString() {
        return "Character{" +
                "preserve=" + preserve +
                ", empty=" + empty +
                ", pixels=" + Arrays.toString(pixels) +
                '}';
    }
}
