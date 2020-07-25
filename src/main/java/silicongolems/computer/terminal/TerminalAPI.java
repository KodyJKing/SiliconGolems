package silicongolems.computer.terminal;

import org.graalvm.polyglot.HostAccess;
import silicongolems.util.Util;

public class TerminalAPI {
    private Terminal terminal;

    public TerminalAPI(Terminal terminal) {
        this.terminal = terminal;
    }

    @HostAccess.Export
    public String getLine(int y) {
        return terminal.getLine(y);
    }
    @HostAccess.Export
    public void setLine(int y, String text) {
        terminal.setLine(y, text);
    }
    @HostAccess.Export
    public int getShift() {
        return terminal.getShift();
    }
    @HostAccess.Export
    public void setShift(int shift) {
        terminal.setShift(shift);
    }
    @HostAccess.Export
    public void print(Object obj) {
        String str = Util.gson.toJson(obj);
        for (String line : Util.printableLines(str, Terminal.WIDTH)) {
            setShift(getShift() + 1);
            setLine(Terminal.HEIGHT - 1, line);
        }
    }
}
