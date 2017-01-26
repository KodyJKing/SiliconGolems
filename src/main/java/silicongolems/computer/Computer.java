package silicongolems.computer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import silicongolems.SiliconGolems;
import silicongolems.gui.GuiScreenTerminal;
import silicongolems.gui.ModGuiHandler;
import silicongolems.network.MessageOpenCloseFile;
import silicongolems.network.MessageTerminalPrint;
import silicongolems.network.ModPacketHandler;
import silicongolems.scripting.Scripting;

import java.util.Stack;

public class Computer {

    /**
     * TODO:
     * Store files, run scripts and maintain terminal information in EntitySiliconGolem class.
     * Move EntitySiliconGolem related data and functionality from EntitySiliconGolem to EntitySiliconGolem.
     * Maintain a map of IDs to EntitySiliconGolem instances to be used to refer to Computers over the net.
     * Pass EntitySiliconGolem refence to guis instead of EntitySiliconGolem.
     * When a player opens a EntitySiliconGolem gui, send the EntitySiliconGolem to the player (not necessarily the whole thing).
     * When a player closes a EntitySiliconGolem gui remove it from the local EntitySiliconGolem map.
     */

    private static int nextID;
    public int id;

    public String activeFile;
    public Stack<String> output;
    static int maxLines = 17;

    public EntityPlayerMP user;
    public World world;

    public Computer(World world, int computerID){
        this.world = world;
        id = computerID;
        Computers.add(this);
        output = new Stack<String>();
        activeFile = "";
    }

    public Computer(World world){
        this(world, nextID++);
    }

    public void executeCommand(String command){
        print(">" + command);
        parseAndRun(command);
    }

    public void parseAndRun(String command){
        String[] words = command.split(" ");
        if(words[0].equals("run"))
            Scripting.runInNewThread(activeFile);
        if(words[0].equals("edit"))
            ModPacketHandler.INSTANCE.sendTo(new MessageOpenCloseFile(this), user);
    }

    public void print(String line){
        printLocal(line);
        if(user != null)
            ModPacketHandler.INSTANCE.sendTo(new MessageTerminalPrint(this, line), user);
    }

    public void printLocal(String line){
        output.push(line);
        if(output.size() > maxLines)
            output.remove(0);
    }

    public void openTerminalGui(EntityPlayer player){
        ModGuiHandler.activeComputer = this;
        player.openGui(SiliconGolems.instance, 0, player.worldObj, 0, 0, 0);
    }

    public void openEditorGui(EntityPlayer player){
        ModGuiHandler.activeComputer = this;
        player.openGui(SiliconGolems.instance, 1, player.worldObj, 0, 0, 0);
    }
}
