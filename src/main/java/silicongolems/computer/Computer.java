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

public class Computer {
    private Thread programThread;
    private boolean isRunning = false;
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
        if (this.bindings != null) bindings.putAll(this.bindings);
        return bindings;
    }

    public class API {
        @HostAccess.Export
        public void sleep(int milis) throws InterruptedException { Thread.sleep(milis); }
        @HostAccess.Export
        public void exit() { Computer.this.killProcess(); }
        @HostAccess.Export
        public void log(Object o) { System.out.println(Util.gson.toJson(o)); }
        @HostAccess.Export
        public Event awaitEvent() {
            boolean empty;
            synchronized (events) { empty = events.isEmpty(); }
            if (empty) {
                synchronized (events) {
                    try {
                        events.wait();
                    } catch (InterruptedException e) {
                        throw new ScriptRuntimeException(e);
                    }
                }
            }
            synchronized (events) { return events.removeLast(); }
        }
    }

    // region operation
    private void startScript() {
        String script = Util.getResource("/assets/silicongolems/js/edit.js");
        runScript(script);
    }

    private void runScript(String script) {
        if (programThread != null) stopScript();
        programThread = new Thread(() -> {
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("graal.js");
            engine.setBindings(createBindingsInstance(), ScriptContext.ENGINE_SCOPE);
            try {
                isRunning = true;
                engine.eval(script);
            } catch (ScriptException e) {
                e.printStackTrace();
            } finally {
                isRunning = false;
            }
        });
        programThread.start();
    }

    private void stopScript() {
        isRunning = false;
        programThread.interrupt();
    }

    public void update() {
        if (terminal != null)
            terminal.update();

        if (!isRunning())
            startScript();

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

    // This is used to impose recovery time after performing certain tasks like moving a golem.
    // I should probably add a special object to wait/notify on.
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
            stopScript();
        }
    }
    // endregion

    public NBTTagCompound writeNBT(NBTTagCompound nbt) {
        return nbt;
    }

    public void readNBT(NBTTagCompound nbt) {
    }
}
