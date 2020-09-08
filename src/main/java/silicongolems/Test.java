package silicongolems;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.ResourceLimits;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import silicongolems.computer.TextBuffer;
import silicongolems.util.RunLengthEncoding;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.function.Consumer;

public class Test {

    public static void main(String[] argv) {
//        textBuffer();
//        runLengthEncoding();
//        autoSerialize();
//        graal();
        graalModules();
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
//        Object foo = new Object() {
//            public int bar = 10;
//            public long baz = 100;
//            public char[] data = new char[100];
//        };
//        ByteBuf buf = Unpooled.buffer();
//        AutoSerialize.serialize(buf, foo);
    }

    private static void graal() {
        try {
            System.out.println(
                System.getProperty("user.dir")
            );
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("graal.js");
            engine.eval("let r = /a/g; let rep = '123'.replace(/3/g, 'three'); print(rep)");
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            Context c = Context.newBuilder("js")
////                    .allowAllAccess(true)
////                    .allowHostAccess(HostAccess.ALL)
//                    .build();
//            c.eval("js","load('./foo.js')");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private static void graalModules() {
    try {
            Context ctx = Context.newBuilder().allowAllAccess(true).option("js.strict", "false").build();
            String module = "function bar() { print('Hello modules!') }";
            String importScript = "load({script:@0, name:'foo.mjs'})".replace("@0", module);
            ctx.eval("js", importScript);
            ctx.eval("js", "bar()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
