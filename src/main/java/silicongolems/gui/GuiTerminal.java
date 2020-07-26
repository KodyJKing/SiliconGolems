package silicongolems.gui;

import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import silicongolems.computer.terminal.Terminal;

import java.io.IOException;

public class GuiTerminal extends GuiScreenText {
    private Terminal terminal;

    public GuiTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    @Override
    public void initGui() {
        super.initGui();
        terminal.onClientOpen();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        terminal.onClientClose();
    }

    @Override
    public void drawTextRegion() {
        if (terminal == null || !terminal.isLoaded()) return;
        try {
            int maxY = Math.min(getTextHeight(), Terminal.HEIGHT);
            int maxX = Math.min(getTextWidth(), Terminal.WIDTH);
            for (int y = 0; y < maxY; y++) {
                String line = terminal.getLine(y);
                for (int x = 0; x < maxX; x++) {
                    drawChar(x, y, line.charAt(x), TextFormatting.WHITE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleKeyboardInput() throws IOException {
        super.handleKeyboardInput();
        Terminal.KeyboardEvent event = new Terminal.KeyboardEvent();
        event.character = Keyboard.getEventCharacter();
        event.keycode = Keyboard.getEventKey();
        event.isDown = Keyboard.getEventKeyState();
        event.isRepeat = Keyboard.isRepeatEvent();
        terminal.input(event);
    }
}
