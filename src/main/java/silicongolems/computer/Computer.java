package silicongolems.computer;

import com.eclipsesource.v8.V8RuntimeException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import scala.Console;
import silicongolems.SiliconGolems;
import silicongolems.util.Util;
import silicongolems.entity.EntitySiliconGolem;
import silicongolems.gui.ModGuiHandler;
import silicongolems.javascript.JSThread;
import silicongolems.network.MessageByte;
import silicongolems.network.MessageOpenCloseFile;
import silicongolems.network.MessagePrint;
import silicongolems.network.ModPacketHandler;
import silicongolems.javascript.WrapperGolem;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Stack;

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

    public Computer(World world, int computerID) {
        this.world = world;
        id = computerID;
        Computers.add(this);
        terminalOutput = new Stack<String>();
        files = new HashMap<String, String>();

        BuiltinScripts.addScripts(this);
    }

    public Computer(World world) {
        this(world, nextID++);
    }

    // region NBT
    public NBTTagCompound writeNBT(NBTTagCompound nbt) {
        nbt.setString("files", Util.gson.toJson(files));
        nbt.setString("terminalOutput", Util.gson.toJson(terminalOutput));
        return nbt;
    }

    public void readNBT(NBTTagCompound nbt) {
        files = Util.gson.fromJson(nbt.getString("files"), files.getClass());
        terminalOutput = Util.gson.fromJson(nbt.getString("terminalOutput"), terminalOutput.getClass());
    }
    // endregion

    // region Commands and Scripting
    public void onInput(String input) {
        if (programThread != null && awaitingInput && programThread.getState() == Thread.State.WAITING)
            inputToProgram(input);
        else if (programThread == null || !programThread.isAlive())
            parseAndRun(input);
    }

    public void inputToProgram(String input) {
        synchronized (programThread) {
            this.input = input;
            awaitingInput = false;
            programThread.notify();
        }
    }

    public void parseAndRun(String command) {
        print(">" + command);

        String[] words = command.split(" ");

        if (words.length < 0)
            return;

        String commandName = words[0];

        switch (commandName) {
            case "edit": {
                String path = getArgument("path", 1, words);
                if (path == null)
                    return;
                ModPacketHandler.INSTANCE.sendTo(new MessageOpenCloseFile(this, path, readOrMakeFile(path)), user);
                return;
            }
            case "rm": {
                String path = getArgument("path", 1, words);
                if (path == null)
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
                if (frompath == null || topath == null || !checkFile(frompath))
                    return;
                writeFile(topath, readOrMakeFile(frompath));
                return;
            }
        }

        if (runProgram(commandName))
            return;

        print("That is not a recognized command!");
    }

    public boolean runProgram(String path) {
        if (files.containsKey(path)) {
            programThread = JSThread.spawnThread(readOrMakeFile(path), getBindings());
            runningProgram = path;
            return true;
        }
        return false;
    }

    public boolean checkFile(String path) {
        if (!files.containsKey(path)) {
            print("There is no file named " + path + ".");
            return false;
        }
        return true;
    }

    public String getArgument(String name, int location, String[] arguments) {
        if (arguments.length <= location) {
            print("Missing " + name + " argument.");
            return null;
        }
        return arguments[location];
    }

    public Object getBindings() {
        Object bindings = new Object() {
            public Object golem = new WrapperGolem(entity);

            public void sleep(int milis) throws InterruptedException {
                Thread.sleep(milis);
            }

            public void print(Object message) {
                addJob(() -> {
                    String repr = message == null ? "null" : message.toString();
                    Computer.this.print(repr);
                });
            }

            public void log(Object message) {
                Console.println(message);
            }

            public String input() throws InterruptedException {
                synchronized (programThread) {
                    if (!programThread.isRunning)
                        throw new Error("Tried to request input while shutting down.");
                    awaitingInput = true;
                    programThread.wait();
                    return input;
                }
                // try {
                // } catch (InterruptedException e) {
                // e.printStackTrace();
                // }
                // return null;
            }

            public void exit() {
                killProcess();
            }
        };

        return bindings;
    }
    // endregion

    // region State
    public void writeFile(String path, String text) {
        files.put(path, text);
    }

    public String readOrMakeFile(String path) {
        if (!files.containsKey(path))
            writeFile(path, "");
        return files.get(path);
    }

    public String readFile(String path) throws Exception {
        if (!files.containsKey(path))
            throw new Exception("File not found!");
        return files.get(path);
    }

    public void print(String line) {
        printLocal(line);
        if (user != null)
            ModPacketHandler.INSTANCE.sendTo(new MessagePrint(this, line), user);
    }

    public void printLocal(String line) {
        for (String subline : Util.printableLines(line, maxTerminalWidth)) {
            subline = Util.removeUnprintable(subline);
            terminalOutput.push(subline);
            if (terminalOutput.size() > maxTerminalLines)
                terminalOutput.remove(0);
        }
    }
    // endregion

    // region Logic and Threading
    public void updateComputer() {
        if (user != null && !inRange(user)) {
            ModPacketHandler.INSTANCE.sendTo(new MessageByte(this, MessageByte.CLOSE_COMPUTER), user);
            user = null;
        }

        // if (programThread != null && !programThread.isAlive()) {
        if (programThread != null && !isRunning() && jobs.isEmpty()) {
            if (programThread.errorMessage == null)
                print(programThread.wasTerminated ? "Program terminated." : "Program finished.");
            else
                print(programThread.errorMessage.replaceAll("<eval>", "\"" + runningProgram + "\""));
            programThread = null;
        }

        if (awaitingUpdate()) {
            synchronized (programThread) {
                programThread.notify();
            }
        }

        synchronized (jobs) {
            while (!jobs.isEmpty())
                jobs.removeLast().run();
        }
    }

    ArrayDeque<Job> jobs = new ArrayDeque<Job>();

    public void addJob(Job job) {
        if (isRunning()) {
            synchronized (jobs) {
                jobs.addFirst(job);
            }
        }
    }

    private boolean awaitingUpdate() {
        return programThread != null && programThread.getState() == Thread.State.WAITING && !awaitingInput;
    }

    private boolean isRunning() {
        return programThread != null && programThread.isRunning && programThread.isAlive();
    }

    public void awaitUpdate(int sleepMilis) {
        synchronized (programThread) {
            try {
                if (sleepMilis > 0)
                    Thread.sleep(sleepMilis);
                programThread.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDestroy() {
        killProcess();
        Computers.remove(this);
    }

    public void killProcess() {
        awaitingInput = false;
        if (programThread == null)
            return;
        synchronized (programThread) {
            programThread.stopScript();
            // print("Terminated program.");
        }
    }

    public boolean canOpen(EntityPlayer player) {
        return user == null && inRange(player);
    }

    public boolean canUse(EntityPlayer player) {
        return player == user;
    }

    public boolean inRange(EntityPlayer player) {
        return entity.getDistanceSq(player.posX, player.posY, player.posZ) < 5 * 5;
    }

    // endregion

    public void openComputerGui(EntityPlayer player) {
        ModGuiHandler.activeComputer = this;
        player.openGui(SiliconGolems.instance, 0, player.world, 0, 0, 0);
    }
}
