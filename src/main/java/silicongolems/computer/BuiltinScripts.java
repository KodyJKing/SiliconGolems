package silicongolems.computer;

import java.util.HashMap;

public class BuiltinScripts {
    public static void addScripts(Computer computer) {
        String js =
                "print(\"Run exit() to leave.\");\n" +
                "while (true) {\n" +
                "    exp = input();\n" +
                "    print(exp);\n" +
                "    try {\n" +
                "        out = eval(exp);\n" +
                "        if (out !== undefined)\n" +
                "            print(out);\n" +
                "    } catch(e) {\n" +
                "        print(e.message);\n" +
                "    }\n" +
                "}";
        computer.writeFile("js", js);
    }
}
