package silicongolems.scripting;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.*;

public class Scripting {
    static ScriptEngine engine;

    public static ScriptEngine getEngine(){
        if(engine != null)
            return engine;
        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        engine = factory.getScriptEngine(new SiliconGolemsFilter());
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        //bindings.remove("print");
        bindings.remove("load");
        bindings.remove("loadWithNewGlobal");
        bindings.remove("exit");
        bindings.remove("quit");
        return engine;
    }

    public static void run(String script, Bindings bindings){
        try {
            if(bindings == null)
                getEngine().eval(script);
            else
                getEngine().eval(script, bindings);
        } catch (ScriptException e){
            System.out.println("There was an issue running the script:\n" + script);
            System.out.println(e.getMessage());
        }
    }

    public static void run(String script){
        run(script, null);
    }

    public static Thread runInNewThread(final String script, final Bindings bindings){
        Thread thread = new Thread(){
            @Override
            public void run() {
                Scripting.run(script, bindings);
            }
        };

        thread.run();
        return thread;
    }

    public static Thread runInNewThread(String script){
        return runInNewThread(script, null);
    }
}
