package silicongolems.computer;

import net.minecraft.nbt.NBTTagCompound;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import silicongolems.computer.filesystem.AssetFileSystem;
import silicongolems.computer.filesystem.FileSystemAPI;
import silicongolems.computer.terminal.Terminal;
import silicongolems.computer.terminal.TerminalAPI;
import silicongolems.util.Util;

import javax.script.*;
import java.util.ArrayDeque;
import java.util.Deque;

public class Computer {
    private Thread programThread;
    private boolean isRunning = false;
    private boolean isDestroyed = false;
    private boolean justStarted = true;
    private Bindings bindings = new SimpleBindings();
    private Deque<Runnable> jobs = new ArrayDeque<>();
    private Deque<Event> events = new ArrayDeque<>();

    public Terminal terminal = new Terminal(this);

    public Bindings getBindings() {
        return bindings;
    }

    private Bindings createBindings() {
        Bindings bindings = new SimpleBindings();
        bindings.put("terminal", new TerminalAPI(terminal, this));
        bindings.put("os", new API());
        if (this.bindings != null)
            bindings.putAll(this.bindings);
        return bindings;
    }

    public class API {
        @HostAccess.Export
        public void sleep(int milis) throws InterruptedException {
            Thread.sleep(milis);
        }

        @HostAccess.Export
        public void exit() {
            Computer.this.killProcess();
        }

        @HostAccess.Export
        public void log(Object obj) {
            System.out.println(obj == null ? "null" : obj.toString());
        }

        @HostAccess.Export
        public Event awaitEvent() {
            boolean empty;
            synchronized (events) {
                empty = events.isEmpty();
            }
            if (empty) {
                synchronized (events) {
                    try {
                        events.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            synchronized (events) {
                return events.removeLast();
            }
        }

        @HostAccess.Export
        public Event dequeueEvent() {
            synchronized (events) {
                if (events.isEmpty())
                    return null;
                return events.removeFirst();
            }
        }

        @HostAccess.Export
        public FileSystemAPI fs = new FileSystemAPI(new AssetFileSystem());
    }

    // region operation
    private void startScript() {
        String script = Util.getResource("/assets/silicongolems/js/js.js");
        runScript(script);
    }

    private static int threadCounter = 0;
    private void runScript(String script) {
        if (programThread != null)
            killProcess();
        if (isDestroyed)
            return;
        programThread = new Thread(() -> {
            Context ctx = Context.newBuilder().option("js.strict", "false").build();
            Value bindings = ctx.getBindings("js");
            Bindings api = createBindings();
            api.forEach((key, val) -> bindings.putMember(key, val));

//             ScriptEngine engine = new ScriptEngineManager().getEngineByName("graal.js");
//             engine.setBindings(new SimpleBindings(), ScriptContext.GLOBAL_SCOPE);
//             engine.setBindings(createBindings(), ScriptContext.ENGINE_SCOPE);
            try {
                isRunning = true;
                ctx.eval("js", script);
//              engine.eval(script);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isRunning = false;
            }
        });
        programThread.setName("JSThread" + threadCounter++);
        programThread.start();
    }

    public void update() {
        if (terminal != null)
            terminal.update();

        if (justStarted) {
            justStarted = false;
            startScript();
        }

        synchronized (programThread) {
            programThread.notify();
        }

        synchronized (jobs) {
            while (!jobs.isEmpty())
                jobs.removeLast().run();
        }
    }

    private boolean isRunning() {
        return programThread != null && isRunning && programThread.isAlive();
    }

    // This is used to impose recovery time after performing certain tasks like moving a golem.
    public void awaitUpdate(int sleepMilis) {
        synchronized (programThread) {
            try {
                if (sleepMilis > 0)
                    Thread.sleep(sleepMilis);
                programThread.wait();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void addJob(Runnable job) {
        if (isRunning()) {
            synchronized (jobs) {
                jobs.addFirst(job);
            }
        } else {
            System.out.print("Tried to add job after program was told to terminate.");
        }
    }

    public void queueEvent(Event event) {
        if (isRunning()) {
            synchronized (events) {
                events.addFirst(event);
                events.notify();
            }
        } else {
            System.out.print("Tried to queue event after program was told to terminate.");
        }
    }

    public void onDestroy() {
        isDestroyed = true;
        killProcess();
        terminal.onDestroy();
    }

    public void killProcess() {
        isRunning = false;
        if (programThread != null)
            programThread.interrupt();
    }
    // endregion

    public NBTTagCompound writeNBT(NBTTagCompound nbt) {
        return nbt;
    }

    public void readNBT(NBTTagCompound nbt) {
    }
}
