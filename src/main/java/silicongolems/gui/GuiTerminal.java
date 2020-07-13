package silicongolems.gui;

import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;
import silicongolems.computer.Terminal;

public class GuiTerminal extends GuiScreenText {
    Terminal terminal; //= createTerminal();

    public GuiTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

//    private static Terminal createTerminal() {
//        Terminal terminal = new Terminal(true);
//        try {
//            terminal.print("Hello\nWorld!");
//            terminal.print("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return terminal;
//    }

    @Override
    public void drawTextRegion() {
        try {
            int maxY = Math.min(getTextHeight(), Terminal.height);
            int maxX = Math.min(getTextWidth(), Terminal.width);
            for (int y = 0; y < maxY; y++) {
                for (int x = 0; x < maxX; x++) {
                    drawChar(x, y, terminal.charAt(x, y), TextFormatting.WHITE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
