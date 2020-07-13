package silicongolems.computer;

import com.eclipsesource.v8.*;
import com.eclipsesource.v8.utils.V8ObjectUtils;
import silicongolems.util.V8Util;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSThread extends Thread {
    String script;
    Object bindings;
    V8 runtime;
    public String errorMessage;
    public  boolean isRunning = true;
    public boolean wasTerminated = false;

    static int threadId = 0;

    public JSThread(String script, Object bindings) {
        super();
        setName("SiliconGolems_JSThread" + threadId++);
        this.script = script;
        this.bindings = bindings;
    }

    public void stopScript() {
        isRunning = false;
        wasTerminated = true;
        runtime.terminateExecution();
        this.interrupt();
    }

    private void releaseRuntime() {
        try {
            runtime.release();
        } catch (IllegalStateException e) {
            System.out.println("MEMORY LEAK DETECTED!");
        }
    }

    @Override
    public void run() {
        super.run();
        try {
            runtime = V8.createV8Runtime();
            V8Util.addBindings(runtime, runtime, bindings);
            if (script.startsWith("'no strict'"))
                runtime.executeScript(script, "program", 0);
            else
                runtime.executeScript("'use strict'; " + script, "program", 0);
        } catch (Exception e) {
            Pattern p = Pattern.compile("program:\\d+: (?<rest>.*)");
            Matcher matcher = p.matcher(e.getMessage());
            if (matcher.matches())
                errorMessage = matcher.group("rest");
        } finally {
            releaseRuntime();
            isRunning = false;
        }
    }

    public static JSThread spawnThread(final String script, final Object bindings) {
        JSThread thread = new JSThread(script, bindings);
        thread.start();
        return thread;
    }

    public static V8Object createObject() {
        Thread thread = Thread.currentThread();
        if (thread instanceof JSThread) {
            JSThread jsThread = (JSThread) thread;
            return new V8Object(jsThread.runtime);
        }
        return null;
    }

    public static V8Object createObject(HashMap<String, Object> map) {
        Thread thread = Thread.currentThread();
        if (thread instanceof JSThread) {
            JSThread jsThread = (JSThread) thread;
            return V8ObjectUtils.toV8Object(jsThread.runtime, map);
        }
        return null;
    }


}
