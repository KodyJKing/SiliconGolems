package silicongolems.network.NetResource;

import java.util.ArrayList;

public class NetResourceTypeRegistry {
    private static ArrayList<Class> classes = new ArrayList<>();
    public static void registerClass(Class clazz) { classes.add(clazz); }
    public static int classId(Class clazz) { return classes.indexOf(clazz); }
    public static Class getClass(int id) { return classes.get(id); }
}
