package silicongolems.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.text.TextFormatting;
import silicongolems.common.Common;
import silicongolems.computer.Computer;
import silicongolems.gui.texteditor.TextEditor;
import silicongolems.network.MessageOpenCloseFile;
import silicongolems.network.MessageOpenCloseTerminal;
import silicongolems.network.ModPacketHandler;

import java.io.IOException;

public class GuiScreenEditor extends GuiScreenText {

    public int scrollX, scrollY;
    public TextEditor editor;

    Computer computer;

    public GuiScreenEditor(Computer computer) {
        editor = new TextEditor();
        scrollX = 0;
        scrollY = 0;
        this.computer = computer;
        editor.type(computer.activeFile);
        clampScroll();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        computer.activeFile = editor.toString();
        ModPacketHandler.INSTANCE.sendToServer(new MessageOpenCloseFile(computer));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        int x, y;
        y = scrollY;
        while(y < editor.lines.size() && y - scrollY < textHeight){
            StringBuilder line = editor.getLine(y);
            x = scrollX;
            while(x < line.length() && x - scrollX < textWidth){
                char c = line.charAt(x);
                if(ChatAllowedCharacters.isAllowedCharacter(c))
                    drawChar(x - scrollX, y - scrollY, c, TextFormatting.GREEN);
                x++;
            }
            y++;
        }

        if(Common.blink(1000, 500))
            drawChar(editor.cursorX - scrollX, editor.cursorY - scrollY, '_', TextFormatting.DARK_GREEN);
    }

    @Override
    protected void keyTyped(char c, int keyCode) throws IOException {
        super.keyTyped(c, keyCode);
        clampScroll();
    }

    //Keep the cursor in view.
    public void clampScroll(){
        scrollX = Common.clamp(editor.cursorX - textWidth + 1, editor.cursorX, scrollX);
        scrollY = Common.clamp(editor.cursorY - textHeight + 1, editor.cursorY, scrollY);
    }

    @Override
    public void onClickCell(int x, int y, int button) {
        editor.cursorX = scrollX + x;
        editor.cursorY = scrollY + y;
        editor.moveCursorY(0);
        editor.moveCursorY(0);
        clampScroll();
    }

    @Override
    public boolean onEscape() {
        Minecraft.getMinecraft().displayGuiScreen(null);
        computer.openTerminalGui(Minecraft.getMinecraft().thePlayer);
        return true;
    }

    @Override
    public void onEnter() {
        editor.newline();
    }

    @Override
    public void onVertArrow(int dir) {
        editor.moveCursorY(dir);
    }

    @Override
    public void onSideArrow(int dir, boolean ctrl) {
        if(ctrl)
            editor.ctrlMove(dir);
        else
            editor.moveCursorX(dir);
    }


    @Override
    public void onBackspace(boolean ctrl) {
        if(ctrl)
            editor.ctrlBackspace();
        else
            editor.backspace();
    }

    @Override
    public void onType(String string) {
        editor.type(string);
    }
}
