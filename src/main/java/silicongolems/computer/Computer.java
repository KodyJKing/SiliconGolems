package silicongolems.computer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import silicongolems.computer.Terminal.Terminal;
import silicongolems.computer.Terminal.TerminalAPI;
import silicongolems.util.Util;
import silicongolems.entity.EntitySiliconGolem;

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
        terminal = new Terminal();
    }

    public NBTTagCompound writeNBT(NBTTagCompound nbt) {
        nbt.setString("files", Util.gson.toJson(files));
        return nbt;
    }

    public void readNBT(NBTTagCompound nbt) {
        files = Util.gson.fromJson(nbt.getString("files"), files.getClass());
    }

    public APIFactory getAPIFactory() {
        return (runtime) -> new Object() {
            public Object golem = new GolemAPI(runtime, entity);
            public Object terminal = new TerminalAPI(Computer.this.terminal);
            public void sleep(int milis) throws InterruptedException { Thread.sleep(milis); }
            public void exit() { killProcess(); }
            public void log(Object message) { System.out.println(message); }
        };
    }

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

    // region operation
    public void startScript() {
        if (programThread != null) programThread.stopScript();
        String script = Util.getResource("/assets/silicongolems/js/edit.js");
        System.out.println(
                "\nScript: \n    " + script.replace("\n","\n     ")
        );
        programThread = JSThread.spawnThread(script, getAPIFactory());
    }

    public void update() {
        if (user != null && !inRange(user))
            user = null;

        if (terminal != null)
            terminal.update();

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
        terminal.onDestroy();
    }

    public void killProcess() {
        if (programThread == null)
            return;
        synchronized (programThread) {
            programThread.stopScript();
        }
    }
    // endregion

    public boolean inRange(EntityPlayer player) {
        return entity.getDistanceSq(player.posX, player.posY, player.posZ) < 5 * 5;
    }
}
