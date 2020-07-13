package silicongolems.util;

import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;

public class SidedIntMaps<T> {
    private HashMap<Integer, T> clientMap = new HashMap<>();
    private HashMap<Integer, T> serverMap = new HashMap<>();
    public HashMap<Integer, T> get(boolean isClient) { return isClient ? clientMap : serverMap; }
    public HashMap<Integer, T> get(Side side) { return side == Side.CLIENT ? clientMap : serverMap; }
}
