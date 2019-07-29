package parsing;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class is used to store a bitmap font.
 *
 * It contains a config with all font parameters and a map of all characterMap. A font is meant to be used
 * hold characterMap for char values ranging from 0 to 255.
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
    private final Map<Integer, Character> characterMap;

    public Font(String name, Config config, Map<Integer, Character> characterMap) {
        this.name = name;
        this.config = config;
        this.characterMap = characterMap;
    }

    public String getName() {
        return name;
    }

    public Config getConfig() {
        return config;
    }

    public Map<Integer, Character> getCharacterMap() {
        return characterMap;
    }

    public List<Character> getCharacters() {
        return this.characterMap.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(e -> e.getKey()))
                .map(e -> e.getValue())
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Font font = (Font) o;
        return Objects.equals(getName(), font.getName()) &&
                Objects.equals(getConfig(), font.getConfig()) &&
                Objects.equals(getCharacterMap(), font.getCharacterMap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getConfig(), getCharacterMap());
    }

    @Override
    public String toString() {
        return "Font{" +
                "name='" + name + '\'' +
                ", config=" + config +
                ", characterMap=" + characterMap +
                '}';
    }
}
