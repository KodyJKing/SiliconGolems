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
import silicongolems.network.MessageOpenCloseFile;
import silicongolems.network.MessagePrint;
import silicongolems.network.ModPacketHandler;
import silicongolems.scripting.Scripting;
import silicongolems.scripting.js.WrapperGolem;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.util.Stack;
import java.util.function.Consumer;

public class Computer {

    private static int nextID;
    public int id;

    public boolean isEditing;
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

    public NBTTagCompound writeNBT(NBTTagCompound tag){
        tag.setString("file", activeFile);
        NBTTagList list = new NBTTagList();
        for(String line : output)
            list.appendTag(new NBTTagString(line));
        tag.setTag("output", list);
        return tag;
    }

    public void readNBT(NBTTagCompound tag){
        activeFile = tag.getString("file");
        NBTTagList list = tag.getTagList("output", 8);
        for(int i = 0; i < list.tagCount(); i++)
            output.push(list.getStringTagAt(i));
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
        if(words[0].equals("run"))
            activeThread = Scripting.runInNewThread(activeFile, getBindings());
        if(words[0].equals("edit"))
            ModPacketHandler.INSTANCE.sendTo(new MessageOpenCloseFile(this), user);
    }

    public void print(String line){
        printLocal(line);
        if(user != null)
            ModPacketHandler.INSTANCE.sendTo(new MessagePrint(this, line), user);
    }

    public void printLocal(String line){
        output.push(line);
        if(output.size() > maxLines)
            output.remove(0);
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
}
