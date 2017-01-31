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

    static int maxTerminalLines = 36;
    static int maxTerminalWidth = 77;
    public Stack<String> terminalOutput;

    public boolean awaitingInput;

    HashMap<String, String> files;

    String input;

    public EntityPlayerMP user;
    public World world;
    public EntitySiliconGolem entity;

    public JSThread programThread;
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

    //region Commands and Scripting
    public void onInput(String input){
        if(programThread != null && awaitingInput && programThread.getState() == Thread.State.WAITING)
            inputToProgram(input);
        else if(programThread == null || !programThread.isAlive())
            parseAndRun(input);
    }

    public void inputToProgram(String input){
        synchronized (programThread){
            this.input = input;
            awaitingInput = false;
            programThread.notify();
        }
    }

    public void parseAndRun(String command){
        print(">" + command);

        String[] words = command.split(" ");

        if(words.length < 0)
            return;

        String commandName = words[0];

        switch (commandName){
            case "edit": {
                String path = getArgument("path", 1, words);
                if(path == null)
                    return;
                ModPacketHandler.INSTANCE.sendTo(new MessageOpenCloseFile(this, path, readOrMakeFile(path)), user);
                return;
            }
            case "rm": {
                String path = getArgument("path", 1, words);
                if(path == null)
                    return;
                files.remove(path);
                return;
            }
            case "clear": {
                terminalOutput.clear();
                ModPacketHandler.INSTANCE.sendTo(new MessageByte(this, MessageByte.CLEAR_SCREEN), user);
                return;
            }
            case "ls": {
                for (String key : files.keySet())
                    print("-" + key);
                return;
            }
            case "cp": {
                String frompath = getArgument("from", 1, words);
                String topath = getArgument("to", 2, words);
                if(frompath == null || topath == null || !checkFile(frompath))
                    return;
                writeFile(topath, readOrMakeFile(frompath));
                return;
            }
        }

        if(files.containsKey(commandName)){
            programThread = Scripting.runInNewThread(readOrMakeFile(commandName), getBindings());
            runningProgram = commandName;
            return;
        }

        print("That is not a recognized command!");
    }

    public boolean checkFile(String path){
        if(!files.containsKey(path)){
            print("There is no file named " + path + ".");
            return false;
        }
        return true;
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
                synchronized (programThread){
                    awaitingInput = true;
                    programThread.wait();
                    return input;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        });

        bindings.put("exit", (BooleanSupplier) () -> {
            programThread.stop();
            return true;
        });

        return bindings;
    }
    //endregion

    //region State
    public void writeFile(String path, String text){
        files.put(path, text);
    }

    public String readOrMakeFile(String path){
        if(!files.containsKey(path))
            writeFile(path, "");
        return files.get(path);
    }

    public String readFile(String path) throws Exception{
        if(!files.containsKey(path))
            throw new Exception("File not found!");
        return files.get(path);
    }

    public void print(String line){
        printLocal(line);
        if(user != null)
            ModPacketHandler.INSTANCE.sendTo(new MessagePrint(this, line), user);
    }

    public void printLocal(String line){
        for(String subline: Common.printableLines(line, maxTerminalWidth)){
            subline = Common.removeUnprintable(subline);
            terminalOutput.push(subline);
            if(terminalOutput.size() > maxTerminalLines)
                terminalOutput.remove(0);
        }
    }
    //endregion

    //region Logic and Threading
    public void updateComputer(){
        if (user != null && !inRange(user)) {
            ModPacketHandler.INSTANCE.sendTo(new MessageByte(this, MessageByte.CLOSE_COMPUTER), user);
            user = null;
        }

        if(programThread != null && !programThread.isAlive()){
            if(programThread.errorMessage == null)
                print("Program finished.");
            else
                print(programThread.errorMessage.replaceAll("<eval>", "\"" + runningProgram + "\""));
            programThread = null;
        }

        if(awaitingUpdate()){
            synchronized (programThread) {programThread.notify();}
        }
    }

    private boolean awaitingUpdate() {
        return programThread != null && programThread.getState() == Thread.State.WAITING && !awaitingInput;
    }

    public void awaitUpdate(int sleepMilis){
        synchronized(programThread) {
            try{
                if(sleepMilis > 0) Thread.sleep(sleepMilis);
                programThread.wait();
            } catch (InterruptedException e) {e.printStackTrace();}
        }
    }

    public void onDestroy(){
        killProcess();
        Computers.remove(this);
    }

    public void killProcess(){
        if(programThread != null){
            programThread.stop();
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

    //endregion

    public void openComputerGui(EntityPlayer player){
        ModGuiHandler.activeComputer = this;
        player.openGui(SiliconGolems.instance, 0, player.worldObj, 0, 0, 0);
    }
}
