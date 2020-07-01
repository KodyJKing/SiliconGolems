package silicongolems.javascript;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import silicongolems.util.ReflectionUtil;

import java.lang.reflect.*;

public class Scripting {
    public static String run(String script, Object bindings) {
        try {
            V8 runtime = V8.createV8Runtime();
            addBindings(runtime, runtime, bindings);
            runtime.executeScript(script);

        } catch (Exception e) {
            System.out.println("There was an issue running the script:\n" + script);
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        return null;
    }

    public static String run(String script) {
        return run("'use strict;'" + script, null);
    }

    public static JSThread runInNewThread(final String script, final Object bindings) {
        JSThread thread = new JSThread(script, bindings);
        thread.setName("SiliconGolems_JSThread");
        thread.start();
        return thread;
    }

    public static JSThread runInNewThread(String script) {
        return runInNewThread(script, null);
    }

    private static void addBindings(V8 runtime, V8Object object, Object bindings) {
        Method[] methods = bindings.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (!ReflectionUtil.isPublic(method)) continue;
            System.out.println("Registering method: " + method.getName());
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
                System.out.println("Adding bindings for field " + fieldName);
                object.add(field.getName(), subObject);
                addBindings(runtime, subObject, subBindings);
                subObject.release();
            } catch (IllegalAccessException e) {
                System.out.println("Couldn't add bindings for " + fieldName + " due to access exception.");
            }
        }
    }

}
