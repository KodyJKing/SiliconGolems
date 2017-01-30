package silicongolems.computer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import silicongolems.SiliconGolems;
import silicongolems.common.Common;
import silicongolems.entity.EntitySiliconGolem;
import silicongolems.gui.ModGuiHandler;
import silicongolems.javascript.JSThread;
import silicongolems.network.MessageByte;
import silicongolems.network.MessageOpenCloseFile;
import silicongolems.network.MessagePrint;
import silicongolems.network.ModPacketHandler;
import silicongolems.javascript.Scripting;
import silicongolems.javascript.js.WrapperGolem;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Computer {

    private static int nextID;
    public int id;

    static int maxTerminalLines = 34;
    public Stack<String> terminalOutput;

    HashMap<String, String> files;

    String input;

    public EntityPlayerMP user;
    public World world;
    public EntitySiliconGolem entity;

    public JSThread activeThread;
    public String runningProgram;

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

    //region NBT
    public NBTTagCompound writeNBT(NBTTagCompound nbt){
        NBTTagCompound filesNbt = new NBTTagCompound();
        for(Map.Entry<String, String> entry: files.entrySet())
            filesNbt.setString(entry.getKey(), entry.getValue());
        nbt.setTag("files", filesNbt);

        NBTTagList terminalNbt = new NBTTagList();
        for(String line : terminalOutput)
            terminalNbt.appendTag(new NBTTagString(line));
        nbt.setTag("terminalOutput", terminalNbt);
        return nbt;
    }

    public void readNBT(NBTTagCompound nbt){
        NBTTagCompound filesNbt = nbt.getCompoundTag("files");
        for(String key: filesNbt.getKeySet())
            files.put(key, filesNbt.getString(key));

        NBTTagList terminalNbt = nbt.getTagList("terminalOutput", 8);
        for(int i = 0; i < terminalNbt.tagCount(); i++)
            terminalOutput.push(terminalNbt.getStringTagAt(i));
    }
    //endregion

    //region Commands
    public void onInput(String input){
        if(activeThread != null && activeThread.getState() == Thread.State.WAITING)
            inputToProgram(input);
        else if(activeThread == null || !activeThread.isAlive())
            parseAndRun(input);
    }

    public void inputToProgram(String input){
        synchronized (activeThread){
            this.input = input;
            activeThread.notify();
        }
    }

    public void parseAndRun(String command){
        print(">" + command);

        String[] words = command.split(" ");

        if(words.length < 0)
            return;

        String commandName = words[0];

        switch (commandName){
            case "edit":
                String path = getArgument("path", 1, words);
                if(path == null)
                    return;
                ModPacketHandler.INSTANCE.sendTo(new MessageOpenCloseFile(this, path, readFile(path)), user);
                return;
            case "rm":
                path = getArgument("path", 1, words);
                if(path == null)
                    return;
                files.remove(path);
                return;
            case "clear":
                terminalOutput.clear();
                ModPacketHandler.INSTANCE.sendTo(new MessageByte(this, MessageByte.CLEAR_SCREEN), user);
                return;
            case "ls":
                for(String key: files.keySet())
                    print("-" + key);
                return;
        }

        if(files.containsKey(commandName)){
            activeThread = Scripting.runInNewThread(readFile(commandName), getBindings());
            runningProgram = commandName;
            return;
        }

        print("That is not a recognized command!");
    }

    public String getArgument(String name, int location, String[] arguments){
        if(arguments.length <= location){
            print("Missing " + name + " argument.");
            return null;
        }
        return  arguments[location];
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

        bindings.put("input", (Supplier<String>) () -> {
            try {
                synchronized (activeThread){
                    activeThread.wait();
                    return input;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        });

        bindings.put("exit", (BooleanSupplier) () -> {
            activeThread.stop();
            return true;
        });

        return bindings;
    }
    //endregion

    //region State
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
        for(String substr: line.split("\n")){
            substr = Common.removeUnprintable(substr);
            terminalOutput.push(substr);
            if(terminalOutput.size() > maxTerminalLines)
                terminalOutput.remove(0);
        }
    }
    //endregion

    //region Logic
    public void updateComputer(){
        if(user != null){
            if(!inRange(user)){
                ModPacketHandler.INSTANCE.sendTo(new MessageByte(this, MessageByte.CLOSE_COMPUTER), user);
                user = null;
            }

            if(activeThread != null && !activeThread.isAlive()){
                if(activeThread.errorMessage == null)
                    print("Program finished.");
                else
                    print(activeThread.errorMessage.replaceAll("<eval>", "\"" + runningProgram + "\""));
                activeThread = null;
            }
        }
    }

    public void onDestroy(){
        killProcess();
        Computers.remove(this);
    }

    public void killProcess(){
        if(activeThread != null){
            activeThread.stop();
            print("Terminated program.");
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

    public void openComputerGui(EntityPlayer player){
        ModGuiHandler.activeComputer = this;
        player.openGui(SiliconGolems.instance, 0, player.worldObj, 0, 0, 0);
    }
    //endregion
}
