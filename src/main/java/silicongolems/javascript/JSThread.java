package silicongolems.javascript;

import javax.script.Bindings;

public class JSThread extends Thread {
    String script;
    Bindings bindings;
    public String errorMessage;

    public JSThread(String script, Bindings bindings) {
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
