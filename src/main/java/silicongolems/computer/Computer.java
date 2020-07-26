package silicongolems.computer;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import net.minecraft.nbt.NBTTagCompound;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import silicongolems.SiliconGolems;
import silicongolems.computer.terminal.Terminal;
import silicongolems.computer.terminal.TerminalAPI;
import silicongolems.util.Util;

import javax.script.*;
import java.util.ArrayDeque;
import java.util.Deque;

/* TODO: 
    - Figure out why repl freezes when holding down enter (and maybe other keys).
    Maybe the fast repeated keyboard events may be putting the threads into a deadlock?
    - Make sure threads don't get stuck waiting.
*/
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

    private Bindings createBindingsInstance() {
        Bindings bindings = new SimpleBindings();
        bindings.put("terminal", new TerminalAPI(terminal));
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
                        events.notify();
                        throw new ScriptRuntimeException(e);
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
                return events.getFirst();
            }
        }
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
            Bindings api = createBindingsInstance();
            api.forEach((key, val) -> ctx.getBindings("js").putMember(key, val));

            // ScriptEngine engine = new ScriptEngineManager().getEngineByName("graal.js");
            // engine.setBindings(createBindingsInstance(), ScriptContext.ENGINE_SCOPE);
            try {
                isRunning = true;
                ctx.eval("js", script);
                // engine.eval(script);
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

    public void addJob(Runnable job) {
        if (isRunning()) {
            synchronized (jobs) {
                jobs.addFirst(job);
            }
        }
    }

    public void queueEvent(Event event) {
        if (isRunning()) {
            synchronized (events) {
                events.addFirst(event);
                events.notify();
            }
        }
    }

    private boolean isRunning() {
        return programThread != null && isRunning && programThread.isAlive();
    }

    private boolean awaitingUpdate() {
        return programThread != null && programThread.getState() == Thread.State.WAITING;
    }

    // This is used to impose recovery time after performing certain tasks like
    // moving a golem.
    // I should probably add a special object to wait/notify on.
    public void awaitUpdate(int sleepMilis) {
        synchronized (programThread) {
            try {
                if (sleepMilis > 0)
                    Thread.sleep(sleepMilis);
                programThread.wait();
            } catch (InterruptedException exception) {
                programThread.notify();
                throw new ScriptRuntimeException(exception);
            }
        }
    }

    public void onDestroy() {
        isDestroyed = true;
        killProcess();
        terminal.onDestroy();
    }

    public void killProcess() {
        isRunning = false;
        if (programThread != null) {
            programThread.stop();
//            Thread monitor = new Thread(() -> {
//                synchronized (programThread) {
//                }
//            });
//            monitor.setName("MonitorThread");
        }
    }
    // endregion

    public NBTTagCompound writeNBT(NBTTagCompound nbt) {
        return nbt;
    }

    public void readNBT(NBTTagCompound nbt) {
    }
}
