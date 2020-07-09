package silicongolems.computer;

import java.util.HashMap;

public class Computers {

    static HashMap<Integer, Computer> serverComputers = new HashMap<>();
    static HashMap<Integer, Computer> clientComputers = new HashMap<>();

    public static Computer getOrCreate(int computerID, boolean isRemote) {
        HashMap<Integer, Computer> map = isRemote ? clientComputers : serverComputers;

        if (!map.containsKey(computerID))
            new Computer(isRemote, computerID);

        return map.get(computerID);
    }

    public static void remove(Computer computer) {
        if (computer.isRemote)
            clientComputers.remove(computer.id);
        else
            serverComputers.remove(computer.id);
    }

    public static Computer add(Computer computer) {
        HashMap<Integer, Computer> map = computer.isRemote ? clientComputers : serverComputers;
        map.put(computer.id, computer);
        return computer;
    }

}
