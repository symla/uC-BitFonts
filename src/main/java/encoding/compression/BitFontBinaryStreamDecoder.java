package encoding.compression;

import parsing.Character;
import parsing.Config;
import parsing.Font;

import java.util.*;

public class BitFontBinaryStreamDecoder {

    private static List<Integer> bytes_to_binary_stream(final byte array[]) {
        final List<Integer> result = new ArrayList<>();
        for ( int i = 0; i < array.length; i++ ) {
            int val = Byte.toUnsignedInt(array[i]);
            for ( int j = 0; j < 8; j++ ) {
                result.add( (val & (int)(Math.pow(2, 7-j))) > 0 ? 1 : 0);
            }
        }
        return result;
    }

    public static Font decodeBitFontsAsBinaryStream(final byte[] array) {
        final int width = Byte.toUnsignedInt(array[0]);
        final int height = Byte.toUnsignedInt(array[1]);
        final Config config = new Config(new byte[]{(byte)width, (byte)height});

        final LinkedList<Integer> bits = new LinkedList<>(bytes_to_binary_stream(array));

        for ( int i = 0; i < 16; i++ ) bits.remove(0);

        final Map<Integer, Character> characters = new HashMap<>();

        for ( int i = 0; i < 256; i++ ) {
            final boolean empty = bits.pop() == 1;
            final boolean preserve = bits.pop() == 1;
            final boolean[] pixels = new boolean[width*height];

            //Skip Pointer
            if ( !empty & preserve ) {
                int skip_amount = read_next_byte(bits);
                for ( int j = 0; j < skip_amount; j++ ) {
                    if ( i+j == 256 ) {
                        break;
                    }
                    characters.put(i+j, new Character(width, height, false, true, 0, 0, pixels, Character.pixels_in_row_to_2d(width, height, pixels)));
                }
                if ( skip_amount == 255 ) break;
                i += skip_amount-2;
            } else if ( !empty ) {
                int offsetTop = 0;
                int offsetBottom = 0;

                while ( bits.pop() == 1 ) {
                    offsetTop++;
                }

                while (bits.pop() == 1 ) {
                    offsetBottom++;
                }

                for ( int j = 0; j < width*height; j++ ) {
                    pixels[j] = bits.pop() == 1;
                }

                characters.put(i, new Character(width, height, preserve, empty, offsetTop, offsetBottom, pixels, Character.pixels_in_row_to_2d(width, height, pixels)));
            } else if ( empty) {
                characters.put(i, new Character(width, height, false, true, 0, 0, pixels, Character.pixels_in_row_to_2d(width, height, pixels)));
            }
        }

        return new Font("Decoded font", config, characters);

    }

    private static int read_next_byte(final LinkedList<Integer> bits) {
        int result = 0;
        for ( int i = 0; i < 8; i++ ) {
            int bit = bits.pop();
            result |= (bit << i);
        }
        return result;
    }

}
