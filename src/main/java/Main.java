import encoding.EncodingUtils;
import encoding.compression.BitFontBinaryStreamDecoder;
import encoding.compression.BitFontBinaryStreamEncoder;
import parsing.BitFontParser;
import parsing.Font;

import java.io.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {

    public static Font parseFont(final String main_PNG_file,
                                 final String offset_preserve_PNG_file,
                                 final String config_PNG_file) {
        if ( main_PNG_file == null ) throw new NullPointerException("main_PNG_file must not be null.");
        if ( offset_preserve_PNG_file == null ) throw new NullPointerException("offset_preserve_PNG_file must not be null.");
        if ( config_PNG_file == null ) throw new NullPointerException("config_PNG_file must not be null.");

        File main_PNG = new File(main_PNG_file);
        File offset_preserve_PNG = new File(offset_preserve_PNG_file);
        File config_PNG = new File(config_PNG_file);

        final long start = System.nanoTime();

        final BitFontParser bitFontParser = new BitFontParser(main_PNG, offset_preserve_PNG, config_PNG);
        final Font parsedFont = bitFontParser.getParsedBitFont();

        final long end = System.nanoTime();
        System.out.println("Took " + ((float) (end - start) / 1000000.0f) + "ms to parse font \""+parsedFont.getName()+"\".");

        return parsedFont;
    }

    public static void encodeFontAsBinaryStream(final Font font) {
        if ( font == null ) throw new NullPointerException("font must not be null.");

        try {
            final long start = System.nanoTime();

            final byte font_bytes[] = BitFontBinaryStreamEncoder.encodeBitFontsAsBinaryStream(font);

            final FileOutputStream fout_bfbs = new FileOutputStream(font.getName() + ".bfbs");
            fout_bfbs.write(font_bytes);
            fout_bfbs.close();

            final StringBuilder sb = new StringBuilder();
            sb.append("/* " +font.getName() + " Font */\n\n");
            sb.append("#ifndef " + font.getName() + "_H_\n");
            sb.append("#define " + font.getName() + "_H_\n\n");
            sb.append("uint8_t " + font.getName() + "[" + font_bytes.length + "] = {\n    ");
            for (int i = 0; i < font_bytes.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append("0x");
                sb.append(String.format("%02x", Byte.toUnsignedInt(font_bytes[i])).toUpperCase());
            }
            sb.append("\n};\n\n#endif\n");

            final FileOutputStream fout_header = new FileOutputStream(font.getName() + ".h");
            fout_header.write(sb.toString().getBytes());
            fout_header.close();

            final long end = System.nanoTime();
            System.out.println("Took " + ((float) (end - start) / 1000000.0f) + "ms to encode and save font \""+font.getName()+"\" as binary stream.");
        } catch ( Exception e ) {
            throw new RuntimeException("Error while encoding font as binary stream.", e);
        }
    }

    public static final String main_AVR_Font_5_by_7 = "TestFonts/AVR_Font_5_by_7/AVR_Font_5_by_7.png";
    public static final String offset_preserve_AVR_Font_5_by_7 = "TestFonts/AVR_Font_5_by_7/AVR_Font_5_by_7_OffsetPreserve.png";
    public static final String config_AVR_Font_5_by_7 = "TestFonts/AVR_Font_5_by_7/AVR_Font_5_by_7_Config.png";

    public static final String main_Icon_Font_12_by_12 = "TestFonts/Icon_Font_12_by_12/Icon_Font_12_by_12.png";
    public static final String offset_preserve_Icon_Font_12_by_12 = "TestFonts/Icon_Font_12_by_12/Icon_Font_12_by_12_OffsetPreserve.png";
    public static final String config_Icon_Font_12_by_12 = "TestFonts/Icon_Font_12_by_12/Icon_Font_12_by_12_Config.png";

    public static final String main_Temp_Font_5_by_13 = "TestFonts/Temp_Font_5_by_13/Temp_Font_5_by_13.png";
    public static final String offset_preserve_Temp_Font_5_by_13 = "TestFonts/Temp_Font_5_by_13/Temp_Font_5_by_13_OffsetPreserve.png";
    public static final String config_Temp_Font_5_by_13 = "TestFonts/Temp_Font_5_by_13/Temp_Font_5_by_13_Config.png";

    public static void main(String args[]) throws Exception {
        /*
        System.out.println("Got " + args.length + " parameters" + (args.length > 0 ? ":" : "."));
        for (String arg : args) {
            System.out.println("\t- " + arg);
        }
        */

        encodeFontAsBinaryStream(
                parseFont(
                        main_AVR_Font_5_by_7,
                        offset_preserve_AVR_Font_5_by_7,
                        config_AVR_Font_5_by_7
                )
        );

        encodeFontAsBinaryStream(
                parseFont(
                        main_Icon_Font_12_by_12,
                        offset_preserve_Icon_Font_12_by_12,
                        config_Icon_Font_12_by_12
                )
        );

        encodeFontAsBinaryStream(
                parseFont(
                        main_Temp_Font_5_by_13,
                        offset_preserve_Temp_Font_5_by_13,
                        config_Temp_Font_5_by_13
                )
        );

    }
}
