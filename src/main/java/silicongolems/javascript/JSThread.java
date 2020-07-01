package silicongolems.javascript;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.V8RuntimeException;
import silicongolems.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
//        this.notify();
    }

    @Override
    public void run() {
        super.run();
        try {
            runtime = V8.createV8Runtime();
            addBindings(runtime, runtime, bindings);
            if (script.startsWith("'no strict'"))
                runtime.executeScript(script, "program", 0);
            else
                runtime.executeScript("'use strict'; " + script, "program", 0);
            runtime.release();
            isRunning = false;
        } catch (Exception e) {
//            if (e instanceof V8RuntimeException) return;
//            System.out.println("There was an issue running the script.");
//            System.out.println(e.getMessage());
            Pattern p = Pattern.compile("program:\\d+: (?<rest>.*)");
            Matcher matcher = p.matcher(e.getMessage());
            if (matcher.matches())
                errorMessage = matcher.group("rest");
        }
    }



    public static JSThread spawnThread(final String script, final Object bindings) {
        JSThread thread = new JSThread(script, bindings);
        thread.start();
        return thread;
    }

    public static void addBindings(V8 runtime, V8Object object, Object bindings) {
        Method[] methods = bindings.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (!ReflectionUtil.isPublic(method)) continue;
//            System.out.println("Registering method: " + method.getName());
            object.registerJavaMethod(
                    bindings,
                    method.getName(),
                    method.getName(),
                    method.getParameterTypes());
        }
        Field[] fields = bindings.getClass().getDeclaredFields();
        for (Field field: fields) {
            String fieldName = field.getName();
            if (!ReflectionUtil.isPublic(field) || fieldName.contains("this")) continue;
            try {
                Object subBindings = ReflectionUtil.forceGet(field, bindings);
                V8Object subObject = new V8Object(runtime);
//                System.out.println("Adding bindings for field " + fieldName);
                object.add(field.getName(), subObject);
                addBindings(runtime, subObject, subBindings);
                subObject.release();
            } catch (IllegalAccessException e) {
                System.out.println("Couldn't add bindings for " + fieldName + " due to access exception.");
            }
        }
    }
}
