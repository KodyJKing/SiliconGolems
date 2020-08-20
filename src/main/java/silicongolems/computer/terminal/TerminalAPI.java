package silicongolems.computer.terminal;

import org.graalvm.polyglot.HostAccess;
import silicongolems.computer.Computer;
import silicongolems.util.Util;

public class TerminalAPI {
    private Terminal terminal;
    private Computer computer;

    public TerminalAPI(Terminal terminal, Computer computer) {
        this.terminal = terminal;
        this.computer = computer;
    }

    @HostAccess.Export
    public int getHeight() {
        return Terminal.HEIGHT;
    }

    @HostAccess.Export
    public int getWidth() {
        return Terminal.WIDTH;
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
        System.out.println(obj);
        String str = obj == null ? "null" : obj.toString();
        for (String line : Util.printableLines(str, Terminal.WIDTH)) {
            setShift(getShift() + 1);
            setLine(Terminal.HEIGHT - 1, line);
        }
    }

    @Override
    @HostAccess.Export
    public String toString() {
        return "Terminal" + terminal.id;
    }
}
