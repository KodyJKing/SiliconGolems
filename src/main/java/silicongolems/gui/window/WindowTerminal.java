package silicongolems.gui.window;

import net.minecraft.util.text.TextFormatting;
import silicongolems.common.Common;
import silicongolems.computer.Computer;
import silicongolems.gui.GuiScreenOS;
import silicongolems.gui.texteditor.TextEditor;
import silicongolems.network.MessageByte;
import silicongolems.network.MessageInput;
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
        output = computer.terminalOutput;
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
            while(x < line.length() && x < getTextWidth()){
                drawChar(x, y, line.charAt(x), TextFormatting.DARK_GREEN);
                x++;
            }
            y++;
        }
    }

    public void drawInput(){
        if(scrollX == 0)
            drawChar(0, getTextHeight() - 1, '>', TextFormatting.GREEN);

        int x = scrollX == 0 ? 0 : scrollX - 1;

        StringBuilder line = input.getLine(0);
        while(x < line.length() && x + 1 - scrollX < getTextWidth()){
            drawChar(x + 1 - scrollX, getTextHeight() - 1, line.charAt(x), TextFormatting.GREEN);
            x++;
        }

        int cursorX = 1 + input.cursorX - scrollX;
        if(cursorX < getTextWidth() && Common.blink(1000, 500))
            drawChar(cursorX, getTextHeight() - 1, '_', TextFormatting.DARK_GREEN);
    }

    @Override
    public void onEnter() {
        String cmd = input.toString();
        cmdHistory.push(cmd);
        input.clear();
        cmdIndex = cmdHistory.size();

        ModPacketHandler.INSTANCE.sendToServer(new MessageInput(computer, cmd));
        clampScroll();
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
        //This statement is odd on purpose, there is an extra +1 because of the '>' character in the input field.
        //Compare to WindowEditor.clampScroll().
        scrollX = Common.clamp(input.cursorX + 1 + 1 - getTextWidth(), input.cursorX, scrollX);
    }

    @Override
    public void onCtrlT() {
        ModPacketHandler.INSTANCE.sendToServer(new MessageByte(computer, MessageByte.TERMINATE));
    }

}
