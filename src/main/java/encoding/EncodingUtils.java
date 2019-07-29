package encoding;

import java.util.ArrayList;
import java.util.List;

public class EncodingUtils {

    public static List<String> bytesToHexStrings(final byte bytes[]) {
        if ( bytes == null ) throw new NullPointerException("bytes must not be null.");

        final List<String> hexStrings = new ArrayList<>();

        for ( byte b : bytes ) {
            hexStrings.add("0x"+String.format("%02x", b));
        }

        return hexStrings;
    }
}
