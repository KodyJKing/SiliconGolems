package silicongolems.gui;

import silicongolems.computer.Computer;
import silicongolems.computer.Computers;
import silicongolems.gui.window.WindowEditor;
import silicongolems.gui.window.WindowTerminal;
import silicongolems.gui.GuiScreenText;
import silicongolems.gui.window.Window;
import silicongolems.network.MessageOpenCloseOS;
import silicongolems.network.ModPacketHandler;

public class GuiScreenOS extends GuiScreenText {

    public Window terminal, editor;
    Computer computer;

    public GuiScreenOS(Computer computer){
        terminal = new WindowTerminal(computer, this);
        this.computer = computer;
    }

    public Window getActiveScreen() {
        if(getEditor() != null)
            return getEditor();
        else
            return terminal;
    }

    private Window getEditor(){
        if(computer.isEditing) {
            if (editor == null)
                editor = new WindowEditor(computer, this);
            return editor;
        } else {
            editor = null;
        }
        return editor;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        getActiveScreen().drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean onEscape() {
        boolean wasEditing = computer.isEditing;
        getActiveScreen().onCloseWindow();
        return wasEditing;
    }

    @Override
    public void onEnter() {
        getActiveScreen().onEnter();
    }

    @Override
    public void onVertArrow(int dir) {
        getActiveScreen().onVertArrow(dir);
    }

    @Override
    public void onSideArrow(int dir, boolean ctrl) {
        getActiveScreen().onSideArrow(dir, ctrl);
    }

    @Override
    public void onBackspace(boolean ctrl) {
        getActiveScreen().onBackspace(ctrl);
    }

    @Override
    public void onType(String string) {
        getActiveScreen().onType(string);
    }

    @Override
    public void onClickCell(int x, int y, int button) {
        getActiveScreen().onClickCell(x, y, button);
    }

    @Override
    public void onGuiClosed() {
        ModPacketHandler.INSTANCE.sendToServer(new MessageOpenCloseOS(computer));
        Computers.remove(computer);
    }
}
