package silicongolems.javascript;

import java.util.Map;

public class JSThread extends Thread {
    String script;
    Map<String, Object> bindings;
    public String errorMessage;

    public JSThread(String script, Map<String, Object> bindings) {
        super();
        this.script = script;
        this.bindings = bindings;
    }

    @Override
    public void run() {
        super.run();
        errorMessage = Scripting.run(script, bindings);
    }
}
