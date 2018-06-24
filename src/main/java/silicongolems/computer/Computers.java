package silicongolems.computer;

import net.minecraft.util.IntHashMap;
import net.minecraft.world.World;

import java.util.HashMap;

public class Computers {

    static HashMap<Integer, Computer> serverComputers = new HashMap<>();
    static HashMap<Integer, Computer> clientComputers = new HashMap<>();

    public static Computer getOrCreate(int computerID, World world) {
        HashMap<Integer, Computer> map = world.isRemote ? clientComputers : serverComputers;

        if (!map.containsKey(computerID))
            new Computer(world, computerID);

        return map.get(computerID);
    }

    public static void remove(Computer computer) {
        if (computer.world.isRemote)
            clientComputers.remove(computer.id);
        else
            serverComputers.remove(computer.id);
    }

    public static Computer add(Computer computer) {
        HashMap<Integer, Computer> map = computer.world.isRemote ? clientComputers : serverComputers;
        map.put(computer.id, computer);
        return computer;
    }

}
