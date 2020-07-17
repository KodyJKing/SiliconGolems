package silicongolems;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import silicongolems.computer.TextBuffer;
import silicongolems.util.AutoSerialize;
import silicongolems.util.RunLengthEncoding;

import java.util.function.Consumer;

public class Test {

    public static void main(String[] argv) {
//        textBuffer();
//        runLengthEncoding();
        autoSerialize();
    }

    private static void textBuffer() {
        TextBuffer buf = new TextBuffer(10, 10);

        Consumer<String> printLine = (line) -> {
            buf.setShift(buf.getShift() + 1);
            buf.setLine(buf.height - 1, line);
        };

        Runnable printTerminal = () -> {
            System.out.println("==============");
            for (int y = 0; y < buf.height; y++)
                System.out.println(buf.getLine(y));
            System.out.println("==============\n");
        };

        for (int i = 0; i < buf.height * 2; i++) {
            printLine.accept("" + i);
            printTerminal.run();
        }
    }

    private static void runLengthEncoding() {
        String message = "\0\0\0\0\0\0\0\0\0\0\0\0           Hello World!";
        String encoded = RunLengthEncoding.encode(message);
        System.out.println(encoded);
        String decoded = RunLengthEncoding.decode(encoded);
        System.out.println(decoded);
        if (!decoded.equals(message))
            throw new AssertionError("Decoded message does not match input!");
    }

    private static void autoSerialize() {
        Object foo = new Object() {
            public int bar = 10;
            public long baz = 100;
            public char[] data = new char[100];
        };
        ByteBuf buf = Unpooled.buffer();
        AutoSerialize.serialize(buf, foo);
    }

}
