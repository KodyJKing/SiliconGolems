package silicongolems;

import silicongolems.computer.TextBuffer;

public class Test {

    public static void main(String[] argv) {
        TextBuffer buf = new TextBuffer(10, 10).init();

        Runnable print = () -> {
            System.out.println("==============");
            for (int y = 0; y < buf.height; y++)
                System.out.println(buf.lineAt(y));
            System.out.println("==============\n");
        };

        for (int i = 0; i < buf.height; i++) {
            buf.print("" + i);
            print.run();
        }

    }

}
