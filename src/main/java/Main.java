import encoding.EncodingUtils;
import encoding.compression.BitFontBinaryStreamEncoder;
import parsing.BitFontParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

public class Main {

    public static void main(String args[]) throws Exception{
        System.out.println("Got "+args.length+" parameters"+(args.length > 0 ? ":" : "."));
        for ( String arg : args ) {
            System.out.println("\t- "+arg);
        }

        File main_PNG = new File("TestFonts/AVR_Font_5_by_7/AVR_Font_5_by_7.png");
        File offset_preserve_PNG = new File("TestFonts/AVR_Font_5_by_7/AVR_Font_5_by_7_OffsetPreserve.png");
        File config_PNG = new File("TestFonts/AVR_Font_5_by_7/AVR_Font_5_by_7_config.png");

        final long start = System.nanoTime();
        final BitFontParser bitFontParser = new BitFontParser(main_PNG, offset_preserve_PNG, config_PNG);
        final long end = System.nanoTime();
        System.out.println("Parsing took "+((float)(end-start)/1000000.0f)+"ms");

        System.out.println();

        final byte fontbytes[] = BitFontBinaryStreamEncoder.encodeBitFontsAsBinaryStream(bitFontParser.getParsedBitFont());

        FileOutputStream fout = new FileOutputStream(bitFontParser.getParsedBitFont().getName()+".bfbs");
        fout.write(fontbytes);
        fout.close();

        System.out.println(EncodingUtils.bytesToHexStrings(fontbytes));
    }
}
