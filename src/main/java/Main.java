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

        final StringBuilder sb = new StringBuilder();
        sb.append("/* "+bitFontParser.getParsedBitFont().getName()+" Font */\n\n");
        sb.append("const uint8_t "+bitFontParser.getParsedBitFont().getName()+"["+fontbytes.length+"] = {\n    ");
        for ( int i = 0; i < fontbytes.length; i++ ) {
            if ( i > 0 ) sb.append(", ");
            sb.append("0x");
            sb.append(String.format("%02x", fontbytes[i]).toUpperCase());
        }
        sb.append("\n};\n");

        FileOutputStream foutHeader = new FileOutputStream(bitFontParser.getParsedBitFont().getName()+".h");
        foutHeader.write(sb.toString().getBytes());
        foutHeader.close();


    }
}
