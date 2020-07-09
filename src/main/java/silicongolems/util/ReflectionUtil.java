package silicongolems.util;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

public class ReflectionUtil {
    public static Object forceGet(Field field, Object object) throws IllegalAccessException {
        boolean wasAccessible = field.isAccessible();
        field.setAccessible(true);
        Object value;
        try {
            value = field.get(object);
        } finally {
            field.setAccessible(wasAccessible);
        }
        return value;
    }

    public static boolean isPublic(Member member) {
        return (member.getModifiers() & Modifier.PUBLIC) > 0;
    }

    public static boolean isPrivate(Member member) {
        return (member.getModifiers() & Modifier.PRIVATE) > 0;
    }
}
