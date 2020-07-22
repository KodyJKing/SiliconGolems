package silicongolems.computer.Terminal;

import com.eclipsesource.v8.V8;
import silicongolems.util.Util;
import silicongolems.util.V8Util;

public class TerminalAPI {
    private Terminal terminal;
    public  TerminalAPI(Terminal terminal) { this.terminal = terminal; }

    public String getLine(int y) { return terminal.getLine(y); }
    public void setLine(int y, String text) { terminal.setLine(y, text); }
    public int getShift() { return terminal.getShift(); }
    public void setShift(int shift) { terminal.setShift(shift); }

    public void print(Object obj) {
        for (String line: Util.printableLines(V8Util.prettyString(obj), Terminal.WIDTH)) {
            setShift(getShift() + 1);
            setLine(Terminal.HEIGHT - 1, line);
        }
    }
}
