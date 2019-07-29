package parsing;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BitFontParser {

    /**
     * Config PNG width to assert.
     */
    private final static int config_PNG_width = 8;

    /**
     * Config PNG height to assert.
     */
    private final static int config_PNG_height = 2;

    /**
     * PNG containing the main tiles (16x16 tiles).
     */
    private final File main_PNG;

    /**
     * PNG with same dimensions as main PNG with encoding of preserve bit and offsets for each character.
     */
    private final File offset_preserve_PNG;

    /**
     * Config PNG.
     */
    private final File config_PNG;

    /**
     * Loaded main PNG.
     */
    private final BufferedImage main_IMG;

    /**
     * Loaded offset & preserve PNG.
     */
    private final BufferedImage offset_preserve_IMG;

    /**
     * Loaded config PNG.
     */
    private final BufferedImage config_IMG;

    /**
     * Parsed bit font.
     */
    private final Font parsedBitFont;

    public BitFontParser(File main_PNG, File offset_preserve_PNG, File config_PNG) {
        if ( main_PNG == null ) throw new NullPointerException("main_PNG must not be null.");
        if ( offset_preserve_PNG == null ) throw new NullPointerException("offset_preserve_PNG must not be null.");
        if ( config_PNG == null ) throw new NullPointerException("config_PNG must not be null.");

        if ( !main_PNG.getName().toLowerCase().endsWith(".png") )
            throw new IllegalArgumentException("Main PNG file's suffix is not '.png', " +
                    "file name was "+main_PNG.getName());

        if ( !offset_preserve_PNG.getName().toLowerCase().endsWith(".png") )
            throw new IllegalArgumentException("Offset & Preserve PNG file's suffix is not '.png', " +
                    "file name was "+offset_preserve_PNG.getName());

        if ( !config_PNG.getName().toLowerCase().endsWith(".png") )
            throw new IllegalArgumentException("Config PNG file's suffix is not '.png', " +
                    "file name was "+config_PNG.getName());

        this.main_PNG = main_PNG;
        this.offset_preserve_PNG = offset_preserve_PNG;
        this.config_PNG = config_PNG;

        try {
            this.main_IMG = ImageIO.read(this.main_PNG);
            this.offset_preserve_IMG = ImageIO.read(this.offset_preserve_PNG);
            this.config_IMG = ImageIO.read(this.config_PNG);
        } catch ( IOException e ) {
            throw new IllegalArgumentException("Failed to load PNG images.", e);
        }

        /* Parse config */
        //int configWidthHeight[] = parseConfigCharWidths(this.config_IMG);
        final byte configBytes[] = parseConfigBytes(this.config_IMG);
        final Config fontConfig = new Config(configBytes);

        /* Parse characters */
        final Map<Integer, Character> characters = this.parseCharacters(this.main_IMG, this.offset_preserve_IMG, fontConfig);
        if ( characters.size() != 256 ) throw new RuntimeException("Parsed characters and got not 255 ones, instead "+characters.size());

        /* Create final font object */
        this.parsedBitFont = new Font(this.main_PNG.getName().substring(0, this.main_PNG.getName().length()-4), fontConfig, characters);
    }

    /**
     * Parses all config bytes.
     * @param config_IMG Config PNG to parse config bytes from.
     * @return Returns array of config bytes.
     */
    private byte[] parseConfigBytes(final BufferedImage config_IMG) {
        if ( config_IMG == null ) throw new NullPointerException("config_IMG must not be null.");

        if ( config_IMG.getWidth() != config_PNG_width )
            throw new IllegalArgumentException("Got config PNG with wrong width. Expected width = "+config_PNG_width+", " +
                    "but got width = "+config_IMG.getWidth()+".");

        if ( config_IMG.getHeight() != config_PNG_height )
            throw new IllegalArgumentException("Got config PNG with wrong height. Expected height = "+config_PNG_height+", " +
                    "but got height = "+config_IMG.getHeight()+".");

        final byte result[] = new byte[2];

        for ( int i = 0; i < result.length; i++ ) {
            result[i] = (byte) this.obtainConfigByte(config_IMG, i);
        }

        return result;
    }

    /**
     * Obtains a byte from the config PNG. Black represent 1s, white 0s. Bytes are aligned row-wise.
     * @param config_IMG Config image. Must be 16x16.
     * @param byteIndex Index of the byte: 0-31.
     * @return Returns parsed byte as int.
     */
    private int obtainConfigByte(final BufferedImage config_IMG, final int byteIndex) {
        if ( config_IMG == null ) throw new NullPointerException("config_IMG must not be null.");

        if ( config_IMG.getWidth() != config_PNG_width )
            throw new IllegalArgumentException("Got config PNG with wrong width. Expected width = 16, " +
                    "but got width = "+config_IMG.getWidth()+".");

        if ( config_IMG.getHeight() != config_PNG_height )
            throw new IllegalArgumentException("Got config PNG with wrong height. Expected height = 16, " +
                    "but got height = "+config_PNG_height+".");

        if ( byteIndex < 0 ) throw new IllegalArgumentException("byteIndex must not be negative, but was "+byteIndex+".");
        if ( byteIndex > 1 ) throw new IllegalArgumentException("byteIndex must not be larger than 31, but was "+byteIndex+".");

        int result = 0x00;

        final int y = byteIndex;
        final int startX = 0;
        for ( int x = startX; x < startX+8; x++ ) {
            final int rgb = config_IMG.getRGB(x, y);
            boolean pixelSet = (rgb & 0x00FFFFFF) == 0x00000000;
            if ( pixelSet ) result |= (1 << (7 - (x-startX)));
        }

        return result;
    }

    /**
     * Parses all characters and its metadata from main IMG and offset & preserve IMG.
     * @param main_IMG Main IMG.
     * @param offset_preserve_IMG Offset&Preserve IMG.
     * @param config Font config.
     * @return Returns characters mapped to char values.
     */
    private Map<Integer, Character> parseCharacters(final BufferedImage main_IMG,
                                                    final BufferedImage offset_preserve_IMG,
                                                    final Config config) {
        if ( main_IMG == null ) throw new NullPointerException("main_IMG must not be null.");
        if ( offset_preserve_IMG == null ) throw new NullPointerException("offset_preserve_IMG must not be null.");
        if ( config == null ) throw new NullPointerException("Config must not be null.");

        final int expectedWidth = 16*config.getCharWidth();
        final int expectedHeight = 16*config.getCharHeight();

        if ( main_IMG.getWidth() != expectedWidth )
            throw new IllegalArgumentException("Got main_IMG with wrong width. Expected width = "+expectedWidth+" " +
                    "(16 tiles with "+config.getCharWidth()+" pixels width), but got width = "+main_IMG.getWidth()+".");

        if ( main_IMG.getHeight() != expectedHeight )
            throw new IllegalArgumentException("Got main_IMG with wrong height. Expected height = "+expectedHeight+" " +
                    "(16 tiles with "+config.getCharHeight()+" pixels height), but got width = "+main_IMG.getHeight()+".");

        if ( offset_preserve_IMG.getWidth() != expectedWidth )
            throw new IllegalArgumentException("Got offset_preserve_IMG with wrong width. Expected width = "+expectedWidth+" " +
                    "(16 tiles with "+config.getCharWidth()+" pixels width), but got width = "+offset_preserve_IMG.getWidth()+".");

        if ( offset_preserve_IMG.getHeight() != expectedHeight )
            throw new IllegalArgumentException("Got offset_preserve_IMG with wrong height. Expected height = "+expectedHeight+" " +
                    "(16 tiles with "+config.getCharHeight()+" pixels height), but got width = "+offset_preserve_IMG.getHeight()+".");

        final Map<Integer, Character> result = new HashMap<>();

        for ( int i = 0; i < 256; i++ ) {
            result.put(i, parseCharacter(main_IMG, offset_preserve_IMG, config, i));
        }

        return result;
    }

    /**
     * Parses a single character.
     * @param main_IMG Main IMG.
     * @param offset_preserve_IMG Offset & Preserve IMG.
     * @param config Font config.
     * @param charIndex Index of character.
     * @return Returns parsed character.
     */
    private Character parseCharacter(final BufferedImage main_IMG,
                                     final BufferedImage offset_preserve_IMG,
                                     final Config config,
                                     final int charIndex) {

        if ( main_IMG == null ) throw new NullPointerException("main_IMG must not be null.");
        if ( offset_preserve_IMG == null ) throw new NullPointerException("offset_preserve_IMG must not be null.");
        if ( config == null ) throw new NullPointerException("Config must not be null.");

        final int expectedWidth = 16*config.getCharWidth();
        final int expectedHeight = 16*config.getCharHeight();

        if ( main_IMG.getWidth() != expectedWidth )
            throw new IllegalArgumentException("Got main_IMG with wrong width. Expected width = "+expectedWidth+" " +
                    "(16 tiles with "+config.getCharWidth()+" pixels width), but got width = "+main_IMG.getWidth()+".");

        if ( main_IMG.getHeight() != expectedHeight )
            throw new IllegalArgumentException("Got main_IMG with wrong height. Expected height = "+expectedHeight+" " +
                    "(16 tiles with "+config.getCharHeight()+" pixels height), but got width = "+main_IMG.getHeight()+".");

        if ( offset_preserve_IMG.getWidth() != expectedWidth )
            throw new IllegalArgumentException("Got offset_preserve_IMG with wrong width. Expected width = "+expectedWidth+" " +
                    "(16 tiles with "+config.getCharWidth()+" pixels width), but got width = "+offset_preserve_IMG.getWidth()+".");

        if ( offset_preserve_IMG.getHeight() != expectedHeight )
            throw new IllegalArgumentException("Got offset_preserve_IMG with wrong height. Expected height = "+expectedHeight+" " +
                    "(16 tiles with "+config.getCharHeight()+" pixels height), but got width = "+offset_preserve_IMG.getHeight()+".");

        if ( charIndex < 0 ) throw new IllegalArgumentException("charIndex must not be negative, but was "+charIndex+".");
        if ( charIndex > 255 ) throw new IllegalArgumentException("charIndex must not be larger than 255, but was "+charIndex+".");

        final int charX = (charIndex * config.getCharWidth()) % (16*config.getCharWidth());
        final int charY = (charIndex / 16) * config.getCharHeight();

        /* Parse plain pixels */
        boolean main_pixelsRow[] = parsePixels(main_IMG, charX, charY, config.getCharWidth(), config.getCharHeight());
        boolean main_pixels2D[][] = parsePixels2D(main_IMG, charX, charY, config.getCharWidth(), config.getCharHeight());

        boolean offset_preserve_pixelsRow[] = parsePixels(offset_preserve_IMG, charX, charY, config.getCharWidth(), config.getCharHeight());

        /* Parse metadata */
        final boolean preserve = offset_preserve_pixelsRow[0];
        boolean empty = true;
        for ( boolean pixel : main_pixelsRow ) {
            if ( pixel ) {
                empty = false;
                break;
            }
        }

        int offsetTop = 0;
        int offsetBottom = 0;

        for ( int i = config.getCharWidth()*1; i < config.getCharWidth()*1+config.getCharWidth(); i++ )
            if ( offset_preserve_pixelsRow[i] ) offsetTop++;

        for ( int i = config.getCharWidth()*2; i < config.getCharWidth()*3+config.getCharWidth(); i++ )
            if ( offset_preserve_pixelsRow[i] ) offsetBottom++;


        return new Character(
                config.getCharWidth(),
                config.getCharHeight(),
                preserve,
                empty,
                offsetTop,
                offsetBottom,
                main_pixelsRow,
                main_pixels2D
        );
    }

    /**
     * Parses pixel area from image. Black pixels = 1s, white ones = 0s.
     * @param img Image to parse from.
     * @param x Start x.
     * @param y Start y.
     * @param width Width of area.
     * @param height Height of area.
     * @return Returns pixels as array in row (left to right, top to bottom).
     */
    private boolean[] parsePixels(final BufferedImage img, final int x, final int y, final int width, final int height) {
        if ( x < 0 ) throw new IllegalArgumentException("x must not be negative, but was "+x+".");
        if ( y < 0 ) throw new IllegalArgumentException("y must not be negative, but was "+y+".");

        if ( x > img.getWidth()-1 ) throw new IllegalArgumentException("x was larger than img.width-1. Got x = "+x+", " +
                "max. x = "+(img.getWidth()-1)+".");
        if ( y > img.getHeight()-1 ) throw new IllegalArgumentException("y was larger than img.height-1. Got y = "+y+", " +
                "max. y = "+(img.getHeight()-1)+".");

        if ( x+width-1 > img.getWidth()-1 ) throw new IllegalArgumentException("x+width-1 was larger than img.width-1. " +
                "Got x+width-1 = "+(x+width-1)+", max. x+width = "+(img.getWidth()-1)+".");
        if ( y+height-1 > img.getHeight()-1 ) throw new IllegalArgumentException("y+height-1 was larger than img.height-1. " +
                "Got y+height-1 = "+(y+height-1)+", max. y+height = "+(img.getHeight()-1)+".");

        boolean result[] = new boolean[width*height];

        for ( int curY = y; curY < y+height; curY++ ) {
            for ( int curX = x; curX < x+width; curX++ ) {
                final int rgb = img.getRGB(curX, curY);
                result[(curY-y)*width+(curX-x)] = (rgb & 0x00FFFFFF) == 0x00000000;
            }
        }

        return result;
    }

    /**
     * Parses pixel area from image. Black pixels = 1s, white ones = 0s. Returns pixels as 2d array.
     * @param img Image to parse from.
     * @param x Start x.
     * @param y Start y.
     * @param width Width of area.
     * @param height Height of area.
     * @return Returns pixels as 2d array (first index: x, second index: y).
     */
    private boolean[][] parsePixels2D(final BufferedImage img, final int x, final int y, final int width, final int height) {
        boolean pixelsInRow[] = parsePixels(img, x, y, width, height);
        boolean result[][] = new boolean[width][height];

        for ( int i = 0; i < pixelsInRow.length; i++ ) {
            result[i % width][i/width] = pixelsInRow[i];
        }

        return result;
    }

    public File getMain_PNG() {
        return main_PNG;
    }

    public File getOffset_preserve_PNG() {
        return offset_preserve_PNG;
    }

    public File getConfig_PNG() {
        return config_PNG;
    }

    public BufferedImage getMain_IMG() {
        return main_IMG;
    }

    public BufferedImage getOffset_preserve_IMG() {
        return offset_preserve_IMG;
    }

    public BufferedImage getConfig_IMG() {
        return config_IMG;
    }

    public Font getParsedBitFont() {
        return parsedBitFont;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BitFontParser that = (BitFontParser) o;
        return Objects.equals(getMain_PNG(), that.getMain_PNG()) &&
                Objects.equals(getOffset_preserve_PNG(), that.getOffset_preserve_PNG()) &&
                Objects.equals(getConfig_PNG(), that.getConfig_PNG()) &&
                Objects.equals(getMain_IMG(), that.getMain_IMG()) &&
                Objects.equals(getOffset_preserve_IMG(), that.getOffset_preserve_IMG()) &&
                Objects.equals(getConfig_IMG(), that.getConfig_IMG()) &&
                Objects.equals(getParsedBitFont(), that.getParsedBitFont());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMain_PNG(), getOffset_preserve_PNG(), getConfig_PNG(), getMain_IMG(), getOffset_preserve_IMG(), getConfig_IMG(), getParsedBitFont());
    }

    @Override
    public String toString() {
        return "BitFontParser{" +
                "main_PNG=" + main_PNG +
                ", offset_preserve_PNG=" + offset_preserve_PNG +
                ", config_PNG=" + config_PNG +
                ", main_IMG=" + main_IMG +
                ", offset_preserve_IMG=" + offset_preserve_IMG +
                ", config_IMG=" + config_IMG +
                ", parsedBitFont=" + parsedBitFont +
                '}';
    }
}
