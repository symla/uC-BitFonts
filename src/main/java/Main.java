import java.io.File;

public class Main {

    public static void main(String args[]) {
        System.out.println("Got "+args.length+" parameters.");
        for ( String arg : args ) {
            System.out.println("\t- "+arg);
        }

        File main_PNG = new File("TestFonts/AVR_Font_5_by_7/AVR_Font_5_by_7.png");
        File offset_preserve_PNG = new File("TestFonts/AVR_Font_5_by_7/AVR_Font_5_by_7_OffsetPreserve.png");
        File config_PNG = new File("TestFonts/AVR_Font_5_by_7/AVR_Font_5_by_7_config.png");
        System.out.println(main_PNG.exists());
        System.out.println(offset_preserve_PNG.exists());
        System.out.println(config_PNG.exists());
    }
}
