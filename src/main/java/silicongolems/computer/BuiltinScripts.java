package silicongolems.computer;

import java.util.HashMap;

public class BuiltinScripts {
    public static void addScripts(Computer computer) {
        String js =
                  "'no strict';"
                + "print(\"Run exit() to leave.\");\n"
                + "while (true) {\n"
                + "    let exp = input();\n"
                + "    print(exp);\n"
                + "    try {\n"
                + "        let out = eval(exp);\n"
                + "        if (out !== undefined)\n"
                + "            print(out);\n"
                + "    } catch(e) {\n"
                + "        print(e.message);\n"
                + "    }\n"
                + "}";
        computer.writeFile("js", js);
    }
}
