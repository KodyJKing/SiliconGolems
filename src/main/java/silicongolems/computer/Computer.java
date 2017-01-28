package silicongolems.computer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import silicongolems.SiliconGolems;
import silicongolems.entity.EntitySiliconGolem;
import silicongolems.gui.ModGuiHandler;
import silicongolems.network.MessageByte;
import silicongolems.network.MessageOpenCloseFile;
import silicongolems.network.MessagePrint;
import silicongolems.network.ModPacketHandler;
import silicongolems.javascript.Scripting;
import silicongolems.javascript.js.WrapperGolem;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.util.HashMap;
import java.util.Stack;
import java.util.function.Consumer;

public class Computer {

    private static int nextID;
    public int id;

    static int maxTerminalLines = 17;
    public Stack<String> terminalOutput;

    HashMap<String, String> files;

    public EntityPlayerMP user;
    public World world;
    public EntitySiliconGolem entity;

    public Thread activeThread;

    public Computer(World world, int computerID){
        this.world = world;
        id = computerID;
        Computers.add(this);
        terminalOutput = new Stack<String>();
        files = new HashMap<String, String>();
    }

    public Computer(World world){
        this(world, nextID++);
    }

    public NBTTagCompound writeNBT(NBTTagCompound tag){
        NBTTagList list = new NBTTagList();
        for(String line : terminalOutput)
            list.appendTag(new NBTTagString(line));
        tag.setTag("terminalOutput", list);
        return tag;
    }

    public void readNBT(NBTTagCompound tag){
        NBTTagList list = tag.getTagList("terminalOutput", 8);
        for(int i = 0; i < list.tagCount(); i++)
            terminalOutput.push(list.getStringTagAt(i));
    }

    public void onDestroy(){
        killProcess();
        Computers.remove(this);
    }

    public void killProcess(){
        activeThread.stop();
    }

    public void executeCommand(String command){
        if(activeThread != null && activeThread.isAlive())
            return;
        print(">" + command);
        parseAndRun(command);
    }

    public void parseAndRun(String command){
        String[] words = command.split(" ");

        if(words.length < 0)
            return;

        String commandName = words[0];

        switch (commandName){
            case "run":
                String path = getArgument("path", 1, words);
                if(path == null)
                    return;
                activeThread = Scripting.runInNewThread(readFile(path), getBindings());
                return;
            case "edit":
                path = getArgument("path", 1, words);
                if(path == null)
                    return;
                ModPacketHandler.INSTANCE.sendTo(new MessageOpenCloseFile(this, path, readFile(path)), user);
                return;
            case "remove":
                path = getArgument("path", 1, words);
                if(path == null)
                    return;
                files.remove(path);
                return;
            case "clear":
                terminalOutput.clear();
                ModPacketHandler.INSTANCE.sendTo(new MessageByte(this, MessageByte.CLEAR_SCREEN), user);
                return;
            default:
                print("That is not a recognized command!");
        }
    }

    public String getArgument(String name, int location, String[] arguments){
        if(arguments.length <= location){
            print("Missing " + name + " argument.");
            return null;
        }
        return  arguments[location];
    }

    public void writeFile(String path, String text){
        files.put(path, text);
    }

    public String readFile(String path){
        if(!files.containsKey(path))
            writeFile(path, "");
        return files.get(path);
    }

    public void print(String line){
        printLocal(line);
        if(user != null)
            ModPacketHandler.INSTANCE.sendTo(new MessagePrint(this, line), user);
    }

    public void printLocal(String line){
        terminalOutput.push(line);
        if(terminalOutput.size() > maxTerminalLines)
            terminalOutput.remove(0);
    }

    public void openOSGui(EntityPlayer player){
        ModGuiHandler.activeComputer = this;
        player.openGui(SiliconGolems.instance, 0, player.worldObj, 0, 0, 0);
    }

    public Bindings getBindings(){
        SimpleBindings bindings = new SimpleBindings();

        bindings.put("golem", new WrapperGolem(entity));

        bindings.put("sleep", (Consumer<Integer>) (Integer milis) -> {
            try{
                Thread.sleep(milis);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        });

        bindings.put("print", (Consumer<Object>) (Object o) -> {print(o.toString());});

        return bindings;
    }

    public void updateComputer(){
        if(user != null){
            if(!inRange(user))
                user = null;
        }
    }

    public boolean canOpen(EntityPlayer player){
        return user == null && inRange(player);
    }

    public boolean canUse(EntityPlayer player){
        return player == user;
    }

    public boolean inRange(EntityPlayer player){
        return entity.getDistanceSq(player.posX, player.posY, player.posZ) < 5 * 5;
    }
}
