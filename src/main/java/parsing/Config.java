package parsing;

import java.util.Arrays;
import java.util.Objects;


/**
 * Font config. Currently only contains width and height of chars.
 */
public class Config {

    /**
     * Index of width byte in config bytes.
     */
    private final static int width_index = 0;

    /**
     * Index of height byte in config bytes.
     */
    private final static int height_index = 1;

    /**
     * Config bytes.
     */
    final byte[] configBytes;

    public Config(final byte[] configBytes) {
        if ( configBytes == null ) throw new NullPointerException("configBytes must not be null.");
        if ( configBytes.length != 32 )
            throw new IllegalArgumentException("configBytes length must be 32, but was "+configBytes.length+".");

        this.configBytes = configBytes;
    }

    public int getCharWidth() {
        return Byte.toUnsignedInt(this.configBytes[width_index]);
    }

    public int getCharHeight() {
        return Byte.toUnsignedInt(this.configBytes[height_index]);
    }

    public static int getWidth_index() {
        return width_index;
    }

    public static int getHeight_index() {
        return height_index;
    }

    public byte[] getConfigBytes() {
        return configBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return Arrays.equals(getConfigBytes(), config.getConfigBytes());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getConfigBytes());
    }

    @Override
    public String toString() {
        return "Config{" +
                "configBytes=" + Arrays.toString(configBytes) +
                '}';
    }
}
