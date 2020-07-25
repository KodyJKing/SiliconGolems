package silicongolems.computer.terminal;

import net.minecraftforge.fml.relauncher.Side;
import silicongolems.util.SidedIntMaps;

public class TerminalRegistry {
    private static final SidedIntMaps<Terminal> INSTANCES = new SidedIntMaps<>();
    static int idCounter = 0;

    public static Terminal getInstance(Side side, int id) { return getInstance(side, id, false); }

    public static Terminal getInstance(Side side, int id, boolean force) {
        Terminal result = INSTANCES.get(side).get(id);
        if (result == null && force) result = new Terminal(side == Side.CLIENT, id);
        return result;
    }

    static void addInstance(Terminal terminal) { INSTANCES.get(terminal.isRemote).put(terminal.id, terminal); }

    static void removeInstance(Terminal terminal) { INSTANCES.get(terminal.isRemote).remove(terminal.id); }
}
