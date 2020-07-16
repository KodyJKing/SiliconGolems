package silicongolems.util;

import com.eclipsesource.v8.*;
import com.eclipsesource.v8.utils.MemoryManager;
import com.eclipsesource.v8.utils.V8ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class V8Util {
    public static void addBindings(V8 runtime, V8Object object, Object bindings) {
        // Add all methods from bindings object.
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

        // Recurse on fields of bindings object.
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

    public static void addWrappedBindings(V8 runtime, Object bindings) {
        HashMap<String, Method> methods = new HashMap<>();
        for (Method m: bindings.getClass().getDeclaredMethods()) {
            if (ReflectionUtil.isPublic(m))
                m.setAccessible(true);
            else
                continue;
            runtime.executeVoidScript("function " + m.getName() + "() { return call('" + m.getName() +"', ...arguments) }");
            methods.put(m.getName() + ":" + m.getParameterCount(), m);
        }

        JavaCallback call = (receiver, args) -> {
            MemoryManager scope = new MemoryManager(runtime);

            String name = args.getString(0);

            Object javaArgs[] = new Object[args.length() - 1];
            for (int i = 1; i < args.length(); i++)
                javaArgs[i - 1] = args.get(i);

            Method m = methods.get(name + ":" + javaArgs.length);
            Object result = null;
            try {
                result = m.invoke(bindings, javaArgs);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            scope.release();
            return result;
        };

        runtime.registerJavaMethod(call, "call");
    }

    public static String prettyString(Object object) {
        String repr = "<cannot print>";
        try {
            if (object == null)
                repr = null;
            else if (object instanceof String)
                repr = (String) object;
            else if (object instanceof V8Array)
                repr = Util.gson.toJson(V8ObjectUtils.toList((V8Array) object));
            else if (object instanceof V8Object && !(object instanceof V8Function))
                repr = Util.gson.toJson(V8ObjectUtils.toMap((V8Object) object));
            else
                repr = object.toString();
        } catch (Exception e) { }
        return repr;
    }
}
