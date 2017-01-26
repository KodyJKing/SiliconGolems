package silicongolems.computer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import silicongolems.SiliconGolems;
import silicongolems.entity.EntitySiliconGolem;
import silicongolems.gui.ModGuiHandler;
import silicongolems.network.MessageOpenCloseFile;
import silicongolems.network.MessageTerminalPrint;
import silicongolems.network.ModPacketHandler;
import silicongolems.scripting.Scripting;
import silicongolems.scripting.js.WrapperGolem;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class Computer {

    private static int nextID;
    public int id;

    public String activeFile;
    public Stack<String> output;
    static int maxLines = 17;

    public EntityPlayerMP user;
    public World world;
    public EntitySiliconGolem entity;

    public Thread activeThread;

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
            activeThread = Scripting.runInNewThread(activeFile, getBindings());
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

    public Bindings getBindings(){
        SimpleBindings bindings = new SimpleBindings();
        bindings.put("golem", new WrapperGolem(entity));
        bindings.put("sleep", new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer milis) {
                try{
                    Thread.sleep(milis);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                return null;
            }
        });
        return bindings;
    }

    public void onDestroy(){
        activeThread.stop();
        Computers.remove(this);
    }
}
