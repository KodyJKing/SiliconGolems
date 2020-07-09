package silicongolems.computer;

import java.util.HashMap;

public class BuiltinScripts {
    public static void addScripts(Computer computer) {
        String js =
                  "'no strict';"
                + "print(\"Run exit() to leave.\");\n"
                + "while (true) {\n"
                + "    var exp = input();\n"
                + "    print(exp);\n"
                + "    try {\n"
                + "        var out = eval(exp);\n"
                + "        if (out !== undefined)\n"
                + "            print(out);\n"
                + "    } catch(e) {\n"
                + "        print(e.message);\n"
                + "    }\n"
                + "}";
        computer.writeFile("js", js);
    }
}
