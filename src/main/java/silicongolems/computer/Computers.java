package silicongolems.computer;

import net.minecraft.util.IntHashMap;
import net.minecraft.world.World;

public class Computers {

    static IntHashMap<Computer> serverComputers = new IntHashMap<Computer>();
    static IntHashMap<Computer> clientComputers = new IntHashMap<Computer>();

    public static Computer getOrCreate(int computerID, World world) {
        IntHashMap<Computer> map = world.isRemote ? clientComputers : serverComputers;

        if(!map.containsItem(computerID)){
            new Computer(world, computerID);
        }
        return map.lookup(computerID);
    }

    public static void remove(Computer computer){
        if(computer.world.isRemote)
            clientComputers.removeObject(computer.id);
        else
            serverComputers.removeObject(computer.id);
    }

    public static Computer add(Computer computer){
        IntHashMap<Computer> map = computer.world.isRemote ? clientComputers : serverComputers;
        map.addKey(computer.id, computer);
        return computer;
    }

}
