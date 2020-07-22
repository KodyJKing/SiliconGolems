package silicongolems.computer;

import com.eclipsesource.v8.*;
import com.eclipsesource.v8.utils.V8ObjectUtils;
import silicongolems.util.V8Util;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSThread extends Thread {
    private static int threadId = 0;
    private String script;
    private APIFactory apiFactory;
    private V8 runtime;
    public String errorMessage;
    public  boolean isRunning = true;
    public boolean wasTerminated = false;

    public JSThread(String script, APIFactory apiFactory) {
        super();
        setName("SiliconGolems_JSThread" + threadId++);
        this.script = script;
        this.apiFactory = apiFactory;
    }

    public static JSThread spawnThread(final String script, final APIFactory apiFactory) {
        JSThread thread = new JSThread(script, apiFactory);
        thread.start();
        return thread;
    }

    @Override
    public void run() {
        super.run();
        try {
            runtime = V8.createV8Runtime();
            Object api = apiFactory.create(runtime);
            V8Util.addBindings(runtime, runtime, api);
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
}
