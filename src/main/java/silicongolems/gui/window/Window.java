package silicongolems.gui.window;

import net.minecraft.util.text.TextFormatting;
import silicongolems.computer.Computer;
import silicongolems.gui.GuiScreenOS;

public class Window {

    public Computer computer;
    public GuiScreenOS gui;

    public Window(Computer computer, GuiScreenOS gui) {
        this.computer = computer;
        this.gui = gui;
    }

    public int cornerX() {
        return gui.cornerX();
    }

    public int cornerY() {
        return gui.cornerY();
    }

    public int textCornerX() {
        return gui.textCornerX();
    }

    public int textCornerY() {
        return gui.textCornerY();
    }

    public int cellX(int textX) {
        return gui.cellX(textX);
    }

    public int cellY(int textY) {
        return gui.cellY(textY);
    }

    public void drawChar(int x, int y, char c, TextFormatting color) {
        gui.drawChar(x, y, c, color);
    }

    public void drawChar(int x, int y, char c, TextFormatting color, boolean fixThin) {
        gui.drawChar(x, y, c, color, fixThin);
    }

    public int getEditorWidth() {
        return gui.getEditorWidth();
    }

    public int getEditorHeight() {
        return gui.getEditorHeight();
    }

    public int getBoarderWidth() {
        return gui.getBoarderWidth();
    }

    public int getTextWidth() {
        return gui.getTextWidth();
    }

    public int getTextHeight() {
        return gui.getTextHeight();
    }

    public int getCharWidth() {
        return gui.getCharWidth();
    }

    public int getCharHeight() {
        return gui.getCharHeight();
    }

    public boolean onEscape() {return false;}
    public void onEnter() {}
    public void onVertArrow(int dir) {}
    public void onSideArrow(int dir, boolean ctrl) {}
    public void onBackspace(boolean ctrl) {}
    public void onType(String string) {}
    public void onClickCell(int x, int y, int button) {}
    public void onOpenWindow() {}
    public void onCloseWindow() {}
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {}
    public void onCtrlT() {}
}
