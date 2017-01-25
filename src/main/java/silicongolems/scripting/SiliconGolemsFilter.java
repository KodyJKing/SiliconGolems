package silicongolems.scripting;

import jdk.nashorn.api.scripting.ClassFilter;

public class SiliconGolemsFilter implements ClassFilter {
    public boolean exposeToScripts(String clazz) {
        return clazz.startsWith("silicongolems.scripting.js.");
    }
}
