package parsing;

import java.util.Map;
import java.util.Objects;

/**
 * This class is used to store a bitmap font.
 *
 * It contains a config with all font parameters and a map of all characters. A font is meant to be used
 * hold characters for char values ranging from 0 to 255.
 */
public class Font {

    /**
     * Name of the font.
     */
    private final String name;

    /**
     * Config of the font.
     */
    private final Config config;

    /**
     * Characters of the font. Char value mapped to character.
     */
    private final Map<Integer, Character> characters;

    public Font(String name, Config config, Map<Integer, Character> characters) {
        this.name = name;
        this.config = config;
        this.characters = characters;
    }

    public String getName() {
        return name;
    }

    public Config getConfig() {
        return config;
    }

    public Map<Integer, Character> getCharacters() {
        return characters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Font font = (Font) o;
        return Objects.equals(getName(), font.getName()) &&
                Objects.equals(getConfig(), font.getConfig()) &&
                Objects.equals(getCharacters(), font.getCharacters());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getConfig(), getCharacters());
    }

    @Override
    public String toString() {
        return "Font{" +
                "name='" + name + '\'' +
                ", config=" + config +
                ", characters=" + characters +
                '}';
    }
}
