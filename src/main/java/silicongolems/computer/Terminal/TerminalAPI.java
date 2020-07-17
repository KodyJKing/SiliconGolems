package silicongolems.computer.Terminal;

public class TerminalAPI {
    private Terminal terminal;
    public  TerminalAPI(Terminal terminal) { this.terminal = terminal; }

    public String getLine(int y) { return terminal.getLine(y); }
    public void setLine(int y, String text) { terminal.setLine(y, text); }
    public int getShift() { return terminal.getShift(); }
    public void setShift(int shift) { terminal.setShift(shift); }
}
