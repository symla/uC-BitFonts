import parsing.BitFontParser;

import java.io.File;

public class Main {

    public static void main(String args[]) {
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
    }
}
