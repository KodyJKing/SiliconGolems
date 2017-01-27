package silicongolems.gui.window;

import net.minecraft.util.text.TextFormatting;
import silicongolems.common.Common;
import silicongolems.computer.Computer;
import silicongolems.gui.GuiScreenOS;
import silicongolems.gui.texteditor.TextEditor;
import silicongolems.network.MessageCommand;
import silicongolems.network.ModPacketHandler;

import java.util.Stack;

public class WindowTerminal extends Window {

    int scrollX;

    int cmdIndex;
    Stack<String> cmdHistory;
    Stack<String> output;
    TextEditor input;

    public WindowTerminal(Computer computer, GuiScreenOS gui){
        super(computer, gui);
        cmdHistory = new Stack<String>();
        output = computer.output;
        cmdIndex = 0;
        input = new TextEditor();

        scrollX = 0;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawOutput();
        drawInput();
    }

    public void drawOutput(){
        int y = 0;
        while(y < output.size() && y < getTextHeight() - 1){
            String line = output.get(y);
            int x = 0;
            while(x < line.length() && x < getTextHeight()){
                drawChar(x, y, line.charAt(x), TextFormatting.DARK_GREEN);
                x++;
            }
            y++;
        }
    }

    public void drawInput(){
        int x = scrollX;

        StringBuilder line = input.getLine(0);
        drawChar(x, getTextHeight() - 1, '>', TextFormatting.GREEN);
        while(x < line.length() && x + 1 - scrollX < getTextWidth()){
            drawChar(x + 1, getTextHeight() - 1, line.charAt(x), TextFormatting.GREEN);
            x++;
        }

        if(Common.blink(1000, 500))
            drawChar(1 + input.cursorX - scrollX, getTextHeight() - 1, '_', TextFormatting.DARK_GREEN);
    }

    @Override
    public void onEnter() {
        String cmd = input.toString();
        cmdHistory.push(cmd);
        input.clear();
        cmdIndex = cmdHistory.size();

        ModPacketHandler.INSTANCE.sendToServer(new MessageCommand(computer, cmd));
    }

    @Override
    public void onVertArrow(int dir) {
        int lastInd = cmdIndex;
        cmdIndex = Common.clamp(0, cmdHistory.size(), cmdIndex + dir);
        if(lastInd == cmdIndex)
            return;

        input.clear();
        if(cmdIndex < cmdHistory.size() && cmdIndex >= 0)
            input.type(cmdHistory.get(cmdIndex));

        clampScroll();
    }

    @Override
    public void onSideArrow(int dir, boolean ctrl) {
        if(ctrl)
            input.ctrlMove(dir);
        else
            input.moveCursorX(dir);
        clampScroll();
    }

    @Override
    public void onBackspace(boolean ctrl) {
        if(ctrl)
            input.ctrlBackspace();
        else
            input.backspace();
        clampScroll();
    }

    @Override
    public void onType(String string) {
        input.type(string);
        clampScroll();
    }

    @Override
    public void onClickCell(int x, int y, int button) {
        input.cursorX = x + scrollX;
        input.clampX();
        clampScroll();
    }

    //Keep the cursor in view.
    public void clampScroll(){
        scrollX = Common.clamp(input.cursorX + 1 - getTextWidth(), input.cursorX, scrollX);
    }
}
