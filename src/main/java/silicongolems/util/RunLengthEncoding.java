package silicongolems.util;

public class RunLengthEncoding {

    private static final char NULL = '\0';
    private static final char ESCAPE = '\0';
    private static final char REPEAT = '\1';
    private static final int MIN_REPEATS = 8;
    private static final int MAX_REPEATS = 256 * 256;

    public static String encode(String text) {
        StringBuilder builder = new StringBuilder();
        char prev = NULL;
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if ((i > 0 && c != prev) || count == MAX_REPEATS) {
                encodeChars(builder, prev, count);
                count = 0;
            }
            count++;
            prev = c;
        }
        encodeChars(builder, prev, count);
        String encodedText = builder.toString();

//        int preLength = text.length();
//        int postLength = encodedText.length();
//        int diff = preLength - postLength;
//        System.out.println(
//            "\nPre length: " + preLength +
//            "\nPost length: " + postLength +
//            "\nDiff: " + diff
//        );

        return encodedText;
    }

    private static void encodeChars(StringBuilder builder, char c, int count) {
        if (count >= MIN_REPEATS) {
            encodeRepeatedChar(builder, c, count);
        } else {
            for (int j = 0; j < count; j++)
                encodeSingleChar(builder, c);
        }
    }

    private static void encodeSingleChar(StringBuilder builder, char c) {
        if (c == NULL)
            builder.append("" + ESCAPE + NULL);
        else
            builder.append(c);
    }

    private static void encodeRepeatedChar(StringBuilder builder, char c, int count) {
        builder.append("" + ESCAPE + REPEAT);
        builder.append((char) count);
        builder.append(c);
    }

    public static String decode(String text) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == ESCAPE) {
                char escapeType = text.charAt(++i);
                // Decode null
                if (escapeType == NULL) {
                    builder.append(NULL);
                    continue;
                }
                // Decode repeat
                int count = (int) text.charAt(++i);
                char k = text.charAt(++i);
                for (int j = 0; j < count; j++)
                    builder.append(k);
            } else {
                // Decode raw
                builder.append(c);
            }
        }
        return builder.toString();
    }
}
