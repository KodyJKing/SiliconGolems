package silicongolems.javascript;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.internal.runtime.Context;

import javax.script.*;

public class Scripting {
    //static ScriptEngine engine;

    public static ScriptEngine getEngine(){
//        if(engine != null)
//            return engine;
        ScriptEngine engine;

        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        engine = factory.getScriptEngine(new JSFilter());
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.remove("print");
        bindings.remove("load");
        bindings.remove("loadWithNewGlobal");
        bindings.remove("exit");
        bindings.remove("quit");

        return engine;
    }

    public static String run(String script, Bindings bindings){
        try {
            ScriptEngine engine = getEngine();
            if(bindings != null)
                engine.getBindings(ScriptContext.ENGINE_SCOPE).putAll(bindings);
            
            engine.eval(script);

        } catch (ScriptException e){
            //System.out.println("There was an issue running the script:\n" + script);
            //System.out.println(e.getMessage());
            return e.getMessage();
        }
        return null;
    }

    public static String run(String script){
        return run(script, null);
    }

    public static JSThread runInNewThread(final String script, final Bindings bindings){
        JSThread thread = new JSThread(script, bindings);
        thread.start();
        return thread;
    }

    public static JSThread runInNewThread(String script){
        return runInNewThread(script, null);
    }
}
