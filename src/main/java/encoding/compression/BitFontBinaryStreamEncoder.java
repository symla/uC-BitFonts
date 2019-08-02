package encoding.compression;

import parsing.Character;
import parsing.Font;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BitFontBinaryStreamEncoder {

    /**
     * Big endian ordering.
     * @param font Font to encode.
     * @return Byte array.
     */
    public static byte[] encodeBitFontsAsBinaryStream(final Font font) {
        if ( font == null ) throw new NullPointerException("Font must not be null.");

        final List<Integer> bits = encodeBitFontAsBitStream(font);
        final int paddingZeros = (8-(bits.size()-(bits.size()/8)*8)) % 8;
        for ( int i = 0; i < paddingZeros; i++ ) bits.add(0);

        final byte bytes[] = new byte[bits.size()/8];
        for ( int i = 0; i < bytes.length; i++ ) {
            for ( int j = 0; j < 8; j++ ) {
                bytes[i] |= ( bits.get(i*8+j) << (7-j));
            }
        }

        return bytes;
    }

    /**
     * Big endian encoding.
     * @param font Font to encode.
     * @return List of bits.
     */
    public static List<Integer> encodeBitFontAsBitStream(final Font font) {
        if ( font == null ) throw new NullPointerException("Font must not be null.");

        final List<Integer> bits = new ArrayList<>();

        bits.addAll(numberToBits(font.getConfig().getCharWidth(), 8));
        bits.addAll(numberToBits(font.getConfig().getCharHeight(), 8));

        final List<Character> characters = font.getCharacters();
        for ( int i = 0; i < characters.size(); i++ ) {
            final Character character = characters.get(i);
            bits.addAll(characterToBitStream(character));

            boolean nextNonEmptyCharacterExists = false;
            int skip_counter = 0;
            for ( int j = i+1; j < characters.size(); j++ ) {
                final Character checkCharacter = characters.get(j);
                if ( checkCharacter.isEmpty() && !character.isPreserve() ) {
                    skip_counter++;
                } else {
                    nextNonEmptyCharacterExists = true;
                    break;
                }
            }

            if ( nextNonEmptyCharacterExists ) {
                if ( skip_counter >= 5 ) {
                    //Add skip pointer
                    bits.addAll(createSkipPointer(skip_counter));
                    i += skip_counter-1;
                } else {
                    // Skip normally with empty & preserve bits
                }
            } else {
                //Indicates skip pointer
                if ( i < characters.size()-1 ) {
                    bits.addAll(createSkipPointer(255));
                    break;
                }
            }
        }

        return bits;
    }

    private static List<Integer> createSkipPointer(final int skip_amount) {
        final List<Integer> result = new ArrayList<>();

        //Indicates skip pointer
        result.add(0);
        result.add(1);

        //Add skip amount
        for ( int i = 0; i < 8; i++ ) {
            result.add( (skip_amount & ((int)Math.pow(2, i))) > 0 ? 1 : 0 );
        }

        return result;
    }

    private static List<Integer> characterToBitStream(final Character character) {
        if ( character == null ) throw new NullPointerException("Character must not be null.");

        final List<Integer> result = new ArrayList<>();

        result.add(character.isEmpty() ? 1 : 0);
        result.add(character.isPreserve() ? 1 : 0);

        if ( !character.isEmpty() ) {
            result.addAll(numberToUnaryWithTrailingZero(character.getOffsetTop()));
            result.addAll(numberToUnaryWithTrailingZero(character.getOffsetBottom()));

            result.addAll(pixelBooleanArrayToBitStream(character.getPixels()));
        }

        return result;
    }

    private static List<Integer> pixelBooleanArrayToBitStream(final boolean pixels[]) {
        if ( pixels == null ) throw new NullPointerException("Pixels must not be null.");
        if ( pixels.length <= 0 ) throw new IllegalArgumentException("Pixels must not be empty.");

        final List<Integer> result = new ArrayList<>();

        for ( int i = 0; i < pixels.length; i++ ) result.add(pixels[i] ? 1 : 0);

        return result;
    }

    private static List<Integer> numberToUnaryWithTrailingZero(final int number) {
        if ( number < 0 ) throw new IllegalArgumentException("Number must not be negative.");

        final List<Integer> result = new ArrayList<>();

        for ( int i = 0; i < number; i++ ) result.add(1);
        result.add(0);

        return result;
    }

    private static List<Integer> numberToBits(final int number, final int digits) {
        final List<Integer> result = new ArrayList<>();

        for ( int i = digits-1; i >= 0; i-- ) {
            result.add((number >> i) & 0x0001);
        }

        return result;
    }

}
