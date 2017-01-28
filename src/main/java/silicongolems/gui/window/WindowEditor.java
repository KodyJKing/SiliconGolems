package silicongolems.gui.window;

import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.text.TextFormatting;
import silicongolems.common.Common;
import silicongolems.computer.Computer;
import silicongolems.gui.GuiScreenOS;
import silicongolems.gui.texteditor.TextEditor;
import silicongolems.network.MessageOpenCloseFile;
import silicongolems.network.ModPacketHandler;

public class WindowEditor extends Window {

    public int scrollX, scrollY;
    public TextEditor editor;
    public String path;

    public WindowEditor(Computer computer, GuiScreenOS gui, String path, String text){
        super(computer, gui);
        scrollX = 0;
        scrollY = 0;
        editor = new TextEditor();
        editor.type(text);
        this.path = path;
        clampScroll();
    }

    @Override
    public void onCloseWindow() {
        ModPacketHandler.INSTANCE.sendToServer(new MessageOpenCloseFile(computer, path, editor.toString()));
        gui.editor = null;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        int x, y;
        y = scrollY;
        while(y < editor.lines.size() && y - scrollY < getTextHeight()){
            StringBuilder line = editor.getLine(y);
            x = scrollX;
            while(x < line.length() && x - scrollX < getTextWidth()){
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

    //Keep the cursor in view.
    public void clampScroll(){
        scrollX = Common.clamp(editor.cursorX - getTextWidth() + 1, editor.cursorX, scrollX);
        scrollY = Common.clamp(editor.cursorY - getTextHeight() + 1, editor.cursorY, scrollY);
    }

    @Override
    public void onClickCell(int x, int y, int button) {
        editor.cursorX = scrollX + x;
        editor.cursorY = scrollY + y;
        editor.clampY();
        clampScroll();
    }

    @Override
    public void onEnter() {
        editor.newline();
        clampScroll();
    }

    @Override
    public void onVertArrow(int dir) {
        editor.moveCursorY(dir);
        clampScroll();
    }

    @Override
    public void onSideArrow(int dir, boolean ctrl) {
        if(ctrl)
            editor.ctrlMove(dir);
        else
            editor.moveCursorX(dir);
        clampScroll();
    }


    @Override
    public void onBackspace(boolean ctrl) {
        if(ctrl)
            editor.ctrlBackspace();
        else
            editor.backspace();
        clampScroll();
    }

    @Override
    public void onType(String string) {
        editor.type(string);
        clampScroll();
    }
}
