package silicongolems.javascript;

import jdk.nashorn.api.scripting.ClassFilter;

public class JSFilter implements ClassFilter {
    public boolean exposeToScripts(String clazz) {
        return clazz.startsWith("silicongolems.javascript.js.");
    }
}
