package silicongolems.gui;

import net.minecraft.util.text.TextFormatting;
import silicongolems.computer.Terminal;

public class GuiTerminal extends GuiScreenText {
    Terminal terminal;

    public GuiTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    @Override
    public void drawTextRegion() {
        try {
            int maxY = Math.min(getTextHeight(), Terminal.height);
            int maxX = Math.min(getTextWidth(), Terminal.width);
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
}
