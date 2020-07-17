package silicongolems.util;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class AutoSerialize {

    public static void serialize(ByteBuf buf, Object obj) {
        Class clazz = obj.getClass();
        for (Field field: clazz.getDeclaredFields()) {
            if (!ReflectionUtil.isPublic(field)) continue;

            Type type = field.getType();
            String typeStr = field.getType().toString();
            try {
                Object value = ReflectionUtil.forceGet(field, obj);
                switch (typeStr) {
                    case "char": case "Char": buf.writeChar((char) value); break;
                    case "int": case "Integer": buf.writeInt((int) value); break;
                    case "long": case "Long": buf.writeLong((long) value); break;
                    case "float": case "Float": buf.writeFloat((float) value); break;
                    case "boolean": case "Boolean": buf.writeBoolean((boolean) value); break;
                    case "String": ByteBufUtils.writeUTF8String(buf, (String) value); break;
                    default:
                        Class fieldClass = (Class) type;
                        if (fieldClass != null) {
                            if (fieldClass.isArray()) {
                                serializeArray(buf, (Object[]) value);
                                break;
                            }
                        }
                        System.out.println("Could not serialize field " + field.toString());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void serializeObject(Type type, Object object) {

    }

    public static void serializeArray(ByteBuf buf, Object[] array) {
        if (array == null)
            buf.writeInt(-1);
        buf.writeInt(array.length);
        for (Object obj: array) {

        }
    }

}
