package silicongolems.computer;

import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Function;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.utils.V8ObjectUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import silicongolems.SiliconGolems;
import silicongolems.util.Util;
import silicongolems.entity.EntitySiliconGolem;
import silicongolems.gui.ModGuiHandler;

import java.util.ArrayDeque;
import java.util.HashMap;

public class Computer {
    HashMap<String, String> files;
    public EntitySiliconGolem entity;
    public EntityPlayerMP user;
    public Terminal terminal;
    public JSThread programThread;
    private boolean justStarted = true;

    public Computer() {
        files = new HashMap<>();
        terminal = new Terminal(false);
    }

    // region NBT
    public NBTTagCompound writeNBT(NBTTagCompound nbt) {
        nbt.setString("files", Util.gson.toJson(files));
//        nbt.setString("terminalOutput", Util.gson.toJson(terminalOutput));
        return nbt;
    }

    public void readNBT(NBTTagCompound nbt) {
        files = Util.gson.fromJson(nbt.getString("files"), files.getClass());
//        terminalOutput = Util.gson.fromJson(nbt.getString("terminalOutput"), terminalOutput.getClass());
    }
    // endregion

    // region Commands and Scripting
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public Object getBindings() {
        Object bindings = new Object() {
            public Object golem = new WrapperGolem(entity);

            public void sleep(int milis) throws InterruptedException {
                Thread.sleep(milis);
            }

            public void print(Object message) {
                String repr = null;
                try {
                    if (message == null)
                        repr = null;
                    else if (message instanceof String)
                        repr = (String) message;
                    else if (message instanceof V8Array)
                        repr = gson.toJson(V8ObjectUtils.toList((V8Array) message));
                    else if (message instanceof V8Object && !(message instanceof V8Function))
                        repr = gson.toJson(V8ObjectUtils.toMap((V8Object) message));
                    else
                        repr = message.toString();
                } catch (Exception e) {
                    System.out.println("Oops! Something went wrong printing this value.");
                    e.printStackTrace();
                }
                String _repr = repr;
                addJob(() -> Computer.this.terminal.print(_repr));
            }

            public void log(Object message) {
                System.out.println(message);
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

    // endregion

    // region Logic and Threading

    public void startScript() {
        if (programThread != null) programThread.stopScript();
        String script = "let i = 0; while (true) { sleep(1000); print(i++); }";
        programThread = JSThread.spawnThread(script, getBindings());
    }

    public void updateComputer() {
        if (user != null && !inRange(user))
            user = null;

        if (justStarted)  {
            startScript();
            justStarted = false;
        }

        synchronized (jobs) {
            while (!jobs.isEmpty())
                jobs.removeLast().run();
        }
    }

    ArrayDeque<Runnable> jobs = new ArrayDeque<>();

    public void addJob(Runnable job) {
        if (isRunning()) {
            synchronized (jobs) {
                jobs.addFirst(job);
            }
        }
    }

    private boolean isRunning() {
        return programThread != null && programThread.isRunning && programThread.isAlive();
    }

    public void awaitUpdate(int sleepMilis) throws InterruptedException {
        synchronized (programThread) {
                if (sleepMilis > 0)
                    Thread.sleep(sleepMilis);
                programThread.wait();
        }
    }

    public void onDestroy() {
        killProcess();
    }

    public void killProcess() {
        if (programThread == null)
            return;
        synchronized (programThread) {
            programThread.stopScript();
        }
    }

    public boolean canOpen(EntityPlayer player) {
        return true;
//        return user == null && inRange(player);
    }

    public boolean canUse(EntityPlayer player) {
        return true;
//        return player == user;
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
