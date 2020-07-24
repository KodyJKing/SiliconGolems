package silicongolems;

import com.oracle.truffle.js.parser.GraalJSEvaluator;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import silicongolems.computer.TextBuffer;
import silicongolems.util.RunLengthEncoding;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class Test {

    public static void main(String[] argv) {
//        textBuffer();
//        runLengthEncoding();
//        autoSerialize();
        graal();
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

//        try {
//            ScriptEngine engine = new ScriptEngineManager().getEngineByName("graal.js");
//            engine.put("foo", new Foo());
//            engine.eval("print(foo.bar)");
//            engine.eval("print(foo.baz)");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        try {
//            Context c = Context.newBuilder("js")
////                    .allowHostAccess(HostAccess.ALL)
//                    .build();
//            HashMap<String, String> map = new HashMap<>();
//            map.put("foo", "bar");
//            c.getBindings("js").putMember("foo", new Foo());
//            c.getBindings("js").putMember("map", map);
//            c.eval("js", "print(foo.bar)");
//            c.eval("js", "print(foo.baz)");
//            c.eval("js", "print(map)");
//            c.eval("js","print(foo.getClass())");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {
            String port = "4242";
            String path = java.util.UUID.randomUUID().toString();
            String remoteConnect = "true";
            Context context = Context.newBuilder("js")
                    .option("inspect", port)
                    .option("inspect.Path", path)
                    .option("inspect.Remote", remoteConnect)
                    .build();
            String hostAdress = "localhost";
            String url = String.format(
                    "chrome-devtools://devtools/bundled/js_app.html?ws=%s:%s/%s",
                    hostAdress, port, path);
            System.out.println(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static class Foo {
        @HostAccess.Export
        public int bar = 42;
        public int baz = 43;
    }

}
