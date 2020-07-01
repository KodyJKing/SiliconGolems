package silicongolems.javascript;

import com.eclipsesource.v8.V8;

public class JSThread extends Thread {
    String script;
    Object bindings;
    public String errorMessage;

    public JSThread(String script, Object bindings) {
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
